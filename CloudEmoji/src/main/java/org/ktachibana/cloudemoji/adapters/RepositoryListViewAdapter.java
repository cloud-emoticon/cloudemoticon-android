package org.ktachibana.cloudemoji.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orm.SugarApp;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryBeginEditingEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.models.Repository;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class RepositoryListViewAdapter extends BaseAdapter {
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
        viewHolder.editButton.setImageDrawable(mContext
                .getResources()
                .getDrawable(R.drawable.ic_edit));
        viewHolder.deleteButton.setImageDrawable(mContext
                        .getResources()
                        .getDrawable(R.drawable.ic_discard)
        );

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

                    // Download repository to file system
                    Ion.with(SugarApp.getSugarContext())
                            .load(item.getUrl())
                            .write(new File(SugarApp.getSugarContext().getFilesDir()
                                    , item.getFileName()))
                            .setCallback(new FutureCallback<File>() {
                                @Override
                                public void onCompleted(Exception e, File result) {
                                    // Dismiss the dialog
                                    dialog.dismiss();

                                    // If no exception, set repository to available and SAVE it
                                    if (e == null) {
                                        item.setAvailable(true);
                                        item.save();
                                    }

                                    /**
                                     * Tell anybody who cares about a repository being downloaded
                                     * Namely the anybody would be repository list fragment
                                     */
                                    EventBus.getDefault().post(new RepositoryDownloadedEvent(item, e));
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
        ImageButton downloadButton;
        @InjectView(R.id.repositoryEditButton)
        ImageButton editButton;
        @InjectView(R.id.repositoryDeleteButton)
        ImageButton deleteButton;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
