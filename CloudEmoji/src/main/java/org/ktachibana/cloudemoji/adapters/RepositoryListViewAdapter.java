package org.ktachibana.cloudemoji.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.orm.SugarApp;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryBeginEditingEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadFailedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.models.Repository;
import org.ktachibana.cloudemoji.models.Source;
import org.ktachibana.cloudemoji.parsing.BackupAndRestoreHelper;
import org.ktachibana.cloudemoji.parsing.SourceJsonParser;
import org.ktachibana.cloudemoji.parsing.SourceParsingException;
import org.ktachibana.cloudemoji.parsing.SourceReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class RepositoryListViewAdapter extends BaseAdapter implements Constants {
    private List<Repository> mRepositories;
    private Context mContext;

    public RepositoryListViewAdapter(Context context) {
        this.mRepositories = Repository.listAll(Repository.class);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mRepositories.size();
    }

    @Override
    public Object getItem(int i) {
        return mRepositories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Standard view holder pattern
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater
                    = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_repository, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Get the item
        final Repository item = mRepositories.get(i);

        // Setup contents
        viewHolder.aliasTextView.setText(item.getAlias());
        viewHolder.urlTextView.setText(item.getUrl());
        viewHolder.downloadButton.setImageDrawable(mContext
                .getResources()
                .getDrawable(
                        item.isAvailable() ? (R.drawable.ic_update) : (R.drawable.ic_download)
                ));

        // Setup what happens if download button is clicked
        viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If network is available
                if (isNetworkConnectionAvailable()) {

                    // Show a dialog progress dialog
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setTitle(R.string.downloading);
                    dialog.setMessage(item.getUrl());
                    dialog.show();

                    new AsyncHttpClient().get(
                        SugarApp.getSugarContext(),
                        item.getUrl(),
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                // Write to file
                                File repositoryFile
                                        = new File(SugarApp.getSugarContext().getFilesDir(), item.getFileName());
                                FileOutputStream outputStream = null;
                                try {
                                    outputStream = new FileOutputStream(repositoryFile);
                                    IOUtils.write(responseBody, outputStream);

                                    // Set repository to available and SAVE it
                                    item.setAvailable(true);
                                    item.save();

                                    /**
                                     * Tell anybody who cares about a repository being downloaded
                                     * Namely the anybody would be repository list fragment
                                     */
                                    EventBus.getDefault().post(new RepositoryDownloadedEvent(item));
                                } catch (Exception e) {
                                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                                } finally {
                                    IOUtils.closeQuietly(outputStream);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                /**
                                 * Tell anybody who cares about a repository download fails
                                 * Namely the anybody would be repository list fragment
                                 */
                                EventBus.getDefault().post(new RepositoryDownloadFailedEvent(error));
                            }

                            @Override
                            public void onFinish() {
                                // Dismiss the dialog
                                dialog.dismiss();
                            }
                        });

                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.bad_conn), Toast.LENGTH_SHORT).show();
                }


            }
        });

        // Setup what happens if edit button is clicked
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Tell anybody who cares about a repository is started edited
                 * Namely the anybody would be repository list fragment
                 */
                EventBus.getDefault().post(new RepositoryBeginEditingEvent(item));
            }
        });

        // Setup what happens if export button is clicked
        viewHolder.exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get Source object
                    Source source = new SourceReader().readSourceFromDatabaseId(item.getId());

                    // Parse Source
                    String json = new SourceJsonParser().serialize(source);

                    // Get file and write
                    String filePath = String.format(EXPORT_FILE_PATH, item.getAlias() + ".json");
                    File exportFile = new File(filePath);
                    new BackupAndRestoreHelper().writeFileToExternalStorage(json, exportFile);

                    Toast.makeText(
                            mContext, filePath, Toast.LENGTH_SHORT).show();
                } catch (SourceParsingException e) {
                    Toast.makeText(
                            mContext,
                            mContext.getString(R.string.invalid_repo_format)+ e.getFormatType().toString(),
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                }
            }
        });

        // Setup what happens if delete button is clicked
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete repository file from file system
                item.delete();
                File deletedFile = new File(SugarApp.getSugarContext().getFilesDir()
                        , item.getFileName());
                deletedFile.delete();

                // Update list
                mRepositories.remove(item);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public void updateRepositories() {
        this.mRepositories = Repository.listAll(Repository.class);
        notifyDataSetChanged();
    }

    private boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    static class ViewHolder {
        @InjectView(R.id.repositoryAliasTextView)
        TextView aliasTextView;
        @InjectView(R.id.repositoryUrlTextView)
        TextView urlTextView;
        @InjectView(R.id.repositoryDownloadButton)
        ImageView downloadButton;
        @InjectView(R.id.repositoryEditButton)
        ImageView editButton;
        @InjectView(R.id.repositoryExportButton)
        ImageView exportButton;
        @InjectView(R.id.repositoryDeleteButton)
        ImageView deleteButton;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
