package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.ktachibana.cloudemoji.BaseApplication;
import org.ktachibana.cloudemoji.BaseBaseAdapter;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryBeginEditingEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadFailedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.events.RepositoryExportEvent;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.ktachibana.cloudemoji.net.RepositoryDownloaderClient;
import org.ktachibana.cloudemoji.parsing.SourceJsonParser;
import org.ktachibana.cloudemoji.parsing.SourceReader;
import org.ktachibana.cloudemoji.ui.NonCancelableProgressMaterialDialogBuilder;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RepositoryListViewAdapter extends BaseBaseAdapter implements Constants {
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
                // Show a dialog progress dialog
                final MaterialDialog dialog = new NonCancelableProgressMaterialDialogBuilder(mContext)
                        .title(R.string.please_wait)
                        .content(mContext.getString(R.string.downloading) + "\n" + item.getUrl())
                        .show();

                new RepositoryDownloaderClient().downloadSource(item, new BaseHttpClient.ObjectCallback<Repository>() {
                    @Override
                    public void success(Repository result) {
                        mBus.post(new RepositoryDownloadedEvent(item));
                    }

                    @Override
                    public void fail(Throwable t) {
                        mBus.post(new RepositoryDownloadFailedEvent(t));
                    }

                    @Override
                    public void finish() {
                        dialog.dismiss();
                    }
                });
            }
        });

        // Setup what happens if edit button is clicked
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBus.post(new RepositoryBeginEditingEvent(item));
            }
        });

        // Setup what happens if export button is clicked
        viewHolder.exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get Source object
                    Source source =
                            new SourceReader().readSourceFromDatabaseId(item.getAlias(), item.getId());

                    // Parse Source
                    String json = new SourceJsonParser().serialize(source);

                    mBus.post(new RepositoryExportEvent(json, item.getAlias()));
                } catch (Exception e) {
                    Log.e(Constants.DEBUG_TAG, e.getLocalizedMessage());
                }
            }
        });

        // Setup what happens if delete button is clicked
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete repository file from file system
                item.delete();
                File deletedFile = new File(BaseApplication.context().getFilesDir(), item.getFileName());
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

    static class ViewHolder {
        @Bind(R.id.repositoryAliasTextView)
        TextView aliasTextView;
        @Bind(R.id.repositoryUrlTextView)
        TextView urlTextView;
        @Bind(R.id.repositoryDownloadButton)
        ImageView downloadButton;
        @Bind(R.id.repositoryEditButton)
        ImageView editButton;
        @Bind(R.id.repositoryExportButton)
        ImageView exportButton;
        @Bind(R.id.repositoryDeleteButton)
        ImageView deleteButton;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
