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

import butterknife.BindView;
import butterknife.ButterKnife;

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

                mBus.post(new FavoriteDeletedEvent(favorite.getEmoticon()));

                mFavorites.remove(favorite);
                notifyDataSetChanged();
            }
        });
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBus.post(new FavoriteBeginEditingEvent(favorite));
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
            int updateLowIndex = from < to ? from : to;
            int updateHighIndex = from < to ? to : from;
            int updateRange = updateHighIndex - updateLowIndex + 1;

            // Shift
            this.mFavorites.add(to, this.mFavorites.remove(from));

            // Clone
            Favorite[] shiftedFavorites = new Favorite[updateRange];
            for (int i = updateLowIndex ; i <= updateHighIndex ; ++i) {
                shiftedFavorites[i - updateLowIndex] = this.mFavorites.get(i).copy();
            }

            // Shift back
            this.mFavorites.add(from, this.mFavorites.remove(to));

            // Overwrite
            for (int i = updateLowIndex ; i <= updateHighIndex ; ++i) {
                this.mFavorites.get(i).overwrite(shiftedFavorites[i - updateLowIndex]);
            }

            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        @BindView(R.id.emoticonTextView)
        TextView emoticon;
        @BindView(R.id.descriptionTextView)
        TextView description;
        @BindView(R.id.favoriteStarImageView)
        ImageView star;
        @BindView(R.id.favoriteEditImageView)
        ImageView edit;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
