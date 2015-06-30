package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import org.ktachibana.cloudemoji.BaseBaseAdapter;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.FavoriteBeginEditingEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.models.disk.Favorite;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FavoriteListViewAdapter extends BaseBaseAdapter implements DragSortListView.DropListener {
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
        if (favorite.getDescription().equals("") && favorite.getShortcut().equals("")) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            String descriptionText =
                    favorite.getDescription().equals("")
                            ? "(" + mContext.getString(R.string.no_description) + ")"
                            : favorite.getDescription();
            String shortcutText =
                    favorite.getShortcut().equals("")
                            ? "(" + mContext.getString(R.string.no_shortcut) + ")"
                            : favorite.getShortcut();
            viewHolder.description.setText(descriptionText + " -> " + shortcutText);
        }
        viewHolder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favorite.delete();

                BUS.post(new FavoriteDeletedEvent(favorite.getEmoticon()));

                mFavorites.remove(favorite);
                notifyDataSetChanged();
            }
        });
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BUS.post(new FavoriteBeginEditingEvent(favorite));
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
            // Load all shifted favorites
            int size = Math.abs(from - to) + 1;
            int lower = Math.min(from, to);
            Favorite[] shiftedFavorites = new Favorite[size];
            for (int i = 0; i < size; i++) {
                shiftedFavorites[i] = (Favorite) getItem(lower + i);
            }

            Favorite firstFavorite = shiftedFavorites[0];
            Favorite lastFavorite = shiftedFavorites[size - 1];
            boolean fromUpperToLower = from < to;

            // If from upper to lower
            if (fromUpperToLower) {
                /**
                 * Those two id's are especially for dragging from up to down
                 * because we have to "roll up" and previous id is always overwritten
                 */
                long replacementId = firstFavorite.getId();
                long currentId;

                // Change first item's id to last item's
                firstFavorite.setId(lastFavorite.getId());

                // Change id's for other items to the item's previous id, except for first item
                for (int i = 1; i < size; i++) {
                    currentId = shiftedFavorites[i].getId();
                    shiftedFavorites[i].setId(replacementId);
                    replacementId = currentId;
                }
            }
            // else from lower to upper
            else {
                /**
                 * This id is especially for dragging from down to up
                 * because when we hit the 2nd last item, it's next id has been changed to a lower id
                 */
                long lastId = lastFavorite.getId();
                // Change last item's id to first item's
                lastFavorite.setId(firstFavorite.getId());

                // Change id's for other items to item's next id, except for last item
                for (int i = 0; i < size - 1; i++) {
                    if (i != size - 2) {
                        shiftedFavorites[i].setId(shiftedFavorites[i + 1].getId());
                    } else {
                        shiftedFavorites[i].setId(lastId);
                    }
                }
            }

            // SAVE and update
            for (Favorite favorite : shiftedFavorites) {
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
