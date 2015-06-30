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
import org.ktachibana.cloudemoji.events.EmptyEvent;
import org.ktachibana.cloudemoji.events.RepositoryAddedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDuplicatedEvent;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.models.memory.StoreRepository;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.Subscribe;

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
        Picasso.with(mContext).load(item.getAuthorIconUrl()).into(viewHolder.authorImage);
        viewHolder.author.setText(item.getAuthor());
        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Repository.hasDuplicateUrl(item.getUrl())) {
                    BUS.post(new RepositoryDuplicatedEvent());
                } else {
                    Repository repository = new Repository(item.getUrl(), item.getAlias());
                    repository.save();
                    BUS.post(new RepositoryAddedEvent(repository));
                }
            }
        });

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.root)
        LinearLayout root;
        @InjectView(R.id.alias)
        TextView alias;
        @InjectView(R.id.url)
        TextView url;
        @InjectView(R.id.description)
        TextView description;
        @InjectView(R.id.authorImage)
        ImageView authorImage;
        @InjectView(R.id.author)
        TextView author;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Subscribe
    public void handle(EmptyEvent event) {

    }
}
