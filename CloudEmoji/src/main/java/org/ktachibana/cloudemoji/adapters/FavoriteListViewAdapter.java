package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldAttributes;
import com.mobeta.android.dslv.DragSortListView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.FavoriteBeginEditingEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.models.Favorite;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class FavoriteListViewAdapter extends BaseAdapter implements DragSortListView.DropListener {
    private Context mContext;
    private List<Favorite> mFavorites;

    public FavoriteListViewAdapter(Context context) {
        this.mContext = context;
        mFavorites = Favorite.listAll(Favorite.class);
    }

    @Override
    public int getCount() {
        return mFavorites.size();
    }

    @Override
    public Object getItem(int i) {
        return mFavorites.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mFavorites.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Standard view holder pattern
        final ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater
                    = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_favorite, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Favorite favorite = mFavorites.get(i);

        // Setup contents
        viewHolder.emoticon.setText(favorite.getEmoticon());
        if (favorite.getDescription().equals("")) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(favorite.getDescription());
        }
        viewHolder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favorite.delete();

                EventBus.getDefault().post(new FavoriteDeletedEvent(favorite.getEmoticon()));

                mFavorites.remove(favorite);
                notifyDataSetChanged();
            }
        });
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Tell anybody who cares about a favorite is being edited
                 * Namely the favorite fragment
                 */
                EventBus.getDefault().post(new FavoriteBeginEditingEvent(favorite));
            }
        });

        return view;
    }

    public void updateFavorites() {
        this.mFavorites = Favorite.listAll(Favorite.class);
        notifyDataSetChanged();
    }

    @Override
    public void drop(int from, int to) {
        // If a valid drag
        if (from != to) {
            // Load all changed favorites
            int size = Math.abs(from - to) + 1;
            int lower = Math.min(from, to);
            Favorite[] changedFavorites = new Favorite[size];
            for (int i = 0; i < size; i++) {
                changedFavorites[i] = (Favorite) getItem(lower + i);
            }

            // Change id of "from" item to id of "to" item
            boolean fromUpToDown = from < to;
            int fromIndex = fromUpToDown ? 0 : size - 1;
            int toIndex = fromUpToDown ? size - 1 : 0;
            Favorite fromItem = changedFavorites[fromIndex];
            Favorite toItem = changedFavorites[toIndex];
            fromItem.setId(toItem.getId());

            /**
             * If dragged from up to down
             * Subtract id's of the rest items by one except for fromItem
             */
            if (fromUpToDown) {
                for (int i = 1; i < size; i++) {
                    changedFavorites[i].setId(changedFavorites[i].getId() - 1);
                }
            }

            /**
             * Else dragged from down to up
             * Add id's of the rest items by one except for toItem
             */
            else
            {
                for (int i = 0; i < size - 1; i++) {
                    changedFavorites[i].setId(changedFavorites[i].getId() + 1);
                }
            }

            // SAVE and update
            for (Favorite favorite : changedFavorites) {
                favorite.save();
            }

            updateFavorites();
        }
    }

    static class ViewHolder {
        @InjectView(R.id.emoticonTextView)
        TextView emoticon;
        @InjectView(R.id.descriptionTextView)
        TextView description;
        @InjectView(R.id.favoriteStarImageView)
        ImageView star;
        @InjectView(R.id.favoriteEditImageView)
        ImageView edit;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
