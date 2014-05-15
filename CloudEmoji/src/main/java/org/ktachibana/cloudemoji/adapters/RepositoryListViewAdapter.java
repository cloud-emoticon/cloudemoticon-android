package org.ktachibana.cloudemoji.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orm.SugarApp;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryDeletedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.models.Repository;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class RepositoryListViewAdapter extends BaseAdapter {
    private List<Repository> items;
    private Context context;

    public RepositoryListViewAdapter(List<Repository> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater
                    = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_repository, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Repository item = items.get(i);

        // Setup contents
        viewHolder.aliasTextView.setText(item.getAlias());
        viewHolder.urlTextView.setText(item.getUrl());
        viewHolder.downloadButton.setImageDrawable(context
                .getResources()
                .getDrawable(
                        item.isAvailable() ? (R.drawable.ic_update) : (R.drawable.ic_download)
                ));
        viewHolder.deleteButton.setImageDrawable(context
                        .getResources()
                        .getDrawable(R.drawable.ic_discard)
        );

        // Setup listeners
        viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = new ProgressDialog(context);
                dialog.setTitle(R.string.downloading);
                dialog.setMessage(item.getUrl());
                dialog.show();
                Ion.with(SugarApp.getSugarContext())
                        .load(item.getUrl())
                        .write(new File(SugarApp.getSugarContext().getFilesDir()
                                , item.getFileName()))
                        .setCallback(new FutureCallback<File>() {
                            @Override
                            public void onCompleted(Exception e, File result) {
                                dialog.dismiss();
                                if (e == null) {
                                    item.setAvailable(true);
                                    item.save();
                                }
                                EventBus.getDefault().post(new RepositoryDownloadedEvent(item, e));
                            }
                        });
            }
        });
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.delete();
                File deletedFile = new File(SugarApp.getSugarContext().getFilesDir()
                        , item.getFileName());
                deletedFile.delete();
                EventBus.getDefault().post(new RepositoryDeletedEvent(item));
            }
        });

        return view;
    }

    public void updateRepositories(List<Repository> repositories) {
        this.items = repositories;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @InjectView(R.id.repositoryAliasTextView)
        TextView aliasTextView;
        @InjectView(R.id.repositoryUrlTextView)
        TextView urlTextView;
        @InjectView(R.id.repositoryDownloadButton)
        ImageButton downloadButton;
        @InjectView(R.id.repositoryDeleteButton)
        ImageButton deleteButton;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
