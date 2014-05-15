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
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_repository, viewGroup, false);
        }
        TextView alias = (TextView) view.findViewById(R.id.repositoryAliasTextView);
        TextView url = (TextView) view.findViewById(R.id.repositoryUrlTextView);
        ImageButton download = (ImageButton) view.findViewById(R.id.repositoryDownloadImageButton);
        ImageButton delete = (ImageButton) view.findViewById(R.id.repositoryDeleteButton);
        final Repository item = items.get(i);
        alias.setText(item.getAlias());
        url.setText(item.getUrl());
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = new ProgressDialog(context);
                dialog.setTitle(R.string.downloading);
                dialog.setMessage(item.getAlias());
                dialog.show();
                Ion.with(SugarApp.getSugarContext())
                        .load(item.getUrl())
                        .write(new File(SugarApp.getSugarContext().getFilesDir(), item.getFileName()))
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
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.delete();
                File deletedFile = new File(SugarApp.getSugarContext().getFilesDir(), item.getFileName());
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
}
