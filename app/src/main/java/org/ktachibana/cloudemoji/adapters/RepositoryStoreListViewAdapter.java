package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ktachibana.cloudemoji.BaseBaseAdapter;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryAddedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDuplicatedEvent;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.models.memory.StoreRepository;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RepositoryStoreListViewAdapter extends BaseBaseAdapter {
    private List<StoreRepository> mRepositories;
    private Context mContext;

    public RepositoryStoreListViewAdapter(Context context, List<StoreRepository> repositories) {
        this.mRepositories = repositories;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mRepositories.size();
    }

    @Override
    public Object getItem(int position) {
        return mRepositories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Standard view holder pattern
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater
                    = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_store_repository, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final StoreRepository item = mRepositories.get(position);

        viewHolder.alias.setText(item.getAlias());
        viewHolder.url.setText(item.getUrl());
        viewHolder.description.setText(item.getDescription());
        Picasso.get().load(item.getAuthorIconUrl()).into(viewHolder.authorImage);
        viewHolder.author.setText(item.getAuthor());
        viewHolder.root.setOnClickListener(v -> {
            if (Repository.hasDuplicateUrl(item.getUrl())) {
                mBus.post(new RepositoryDuplicatedEvent());
            } else {
                Repository repository = new Repository(item.getUrl(), item.getAlias());
                repository.save();
                mBus.post(new RepositoryAddedEvent(repository));
            }
        });

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.root)
        LinearLayout root;
        @Bind(R.id.alias)
        TextView alias;
        @Bind(R.id.url)
        TextView url;
        @Bind(R.id.description)
        TextView description;
        @Bind(R.id.authorImage)
        ImageView authorImage;
        @Bind(R.id.author)
        TextView author;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
