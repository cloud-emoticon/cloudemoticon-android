package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
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
import org.ktachibana.cloudemoji.ui.RoundedBackgroundSpan;
import org.ktachibana.cloudemoji.ui.ScrollableEmoticonMaterialDialogBuilder;
import org.ktachibana.cloudemoji.utils.CapabilityUtils;

import java.util.List;

import butterknife.Bind;
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

        // Setup emoticon text
        viewHolder.emoticon.setText(favorite.getEmoticon());

        // Setup description text
        final String description = favorite.getDescription();
        final String shortcut = !CapabilityUtils.disableFavoriteShortcutFeature() ? favorite.getShortcut() : "";
        if (description.equals("") && shortcut.equals("")) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            Spannable descriptionText;
            if (description.equals("")) {
                // hack a space at end to make it show??
                descriptionText = new SpannableString(shortcut + " ");
                descriptionText.setSpan(
                        new RoundedBackgroundSpan(
                                Color.GRAY, Color.WHITE,
                                10, 10, 5, 5),
                        0,
                        shortcut.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (shortcut.equals("")) {
                descriptionText = new SpannableString(description);
            } else {
                descriptionText = new SpannableString(description + " " + shortcut);
                descriptionText.setSpan(
                        new RoundedBackgroundSpan(
                                Color.GRAY, Color.WHITE,
                                10, 10, 5, 5),
                        description.length() + 1,
                        description.length() + 1 + shortcut.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            viewHolder.description.setText(descriptionText);
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
        @Bind(R.id.emoticonTextView)
        TextView emoticon;
        @Bind(R.id.descriptionTextView)
        TextView description;
        @Bind(R.id.favoriteStarImageView)
        ImageView star;
        @Bind(R.id.favoriteEditImageView)
        ImageView edit;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
