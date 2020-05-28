package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.collection.ArrayMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.EmptyEvent;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.memory.Category;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.models.memory.Source;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

public class SourceListViewAdapter extends SectionedBaseAdapter implements SectionIndexer {
    // Constant drawables
    Drawable mNoStarDrawable;
    Drawable mStarDrawable;
    private Source mSource;
    private LayoutInflater mInflater;
    // Cache stores whether a emoticon is in favorites
    private ArrayMap<String, Boolean> mEmoticonInFavoritesCache;

    // Section headers
    private String[] mSectionHeaders;

    // Section index to adapter position mapping
    private List<Integer> mSectionIndexToPositionMapping;

    public SourceListViewAdapter(Context context, Source source) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSource = source;
        mEmoticonInFavoritesCache = new ArrayMap<>();

        // Constant drawables
        mNoStarDrawable = context.getResources().getDrawable(R.drawable.ic_unfavorite);
        mStarDrawable = context.getResources().getDrawable(R.drawable.ic_favorite);

        // Section headers
        mSectionHeaders = new String[mSource.getCategories().size()];
        for (int i = 0; i < mSectionHeaders.length; i++) {
            mSectionHeaders[i] = mSource.getCategories().get(i).getName();
        }

        // Section index to adapter position mapping
        mSectionIndexToPositionMapping = new ArrayList<>();
        List<Integer> positionsToSectionsMapping = new ArrayList<>();
        int section = 0;
        for (Category category : mSource.getCategories()) {
            for (Entry entry : category.getEntries()) {
                positionsToSectionsMapping.add(section);
            }
            section++;
        }

        mSectionIndexToPositionMapping.add(0);
        for (int j = 1; j < positionsToSectionsMapping.size(); j++) {
            if (!positionsToSectionsMapping.get(j).equals(positionsToSectionsMapping.get(j - 1))) {
                mSectionIndexToPositionMapping.add(j);
            }
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public Object getItem(int section, int position) {
        return mSource.getCategories().get(section).getEntries().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return 0;
    }

    @Override
    public int getSectionCount() {
        return mSource.getCategories().size();
    }

    @Override
    public int getCountForSection(int section) {
        return mSource.getCategories().get(section).getEntries().size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        // Standard view holder pattern
        final EntryViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_entry, parent, false);
            viewHolder = new EntryViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (EntryViewHolder) convertView.getTag();
        }

        // Setup contents
        final Entry entry = (Entry) getItem(section, position);
        final String emoticon = entry.getEmoticon();
        final String description = entry.getDescription();
        viewHolder.emoticon.setText(emoticon);

        // Set description GONE if no description
        boolean hasDescription = description.equals("");
        if (hasDescription) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(description);
        }

        // Setup star
        Boolean isInCache = mEmoticonInFavoritesCache.get(emoticon);
        // If not in cache, then do query and put into cache
        if (isInCache == null) {
            boolean isInFavorites = Favorite.queryByEmoticon(emoticon) != null;
            mEmoticonInFavoritesCache.put(emoticon, isInFavorites);
        }
        // Else it is in cache, retrieve
        final boolean isStared = mEmoticonInFavoritesCache.get(emoticon);
        viewHolder.favorite.setImageDrawable(isStared ? mStarDrawable : mNoStarDrawable);

        // Setup on star clicked
        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrRemoveFromFavorites(isStared, emoticon, viewHolder, description);

            }
        });

        return convertView;
    }

    private void addOrRemoveFromFavorites(boolean isStared, String emoticon, EntryViewHolder viewHolder, String description) {
        // If already in favorite, remove it from favorites
        if (isStared) {
            // Remove from database
            Favorite favorite = Favorite.queryByEmoticon(emoticon);
            if (favorite != null) {
                favorite.delete();
            }
            // Update cache
            mEmoticonInFavoritesCache.put(emoticon, false);
            // Update this star
            viewHolder.favorite.setImageDrawable(mNoStarDrawable);
            // Update other stars
            notifyDataSetChanged();
            // Notify main activity
            EventBus.getDefault().post(new FavoriteDeletedEvent(emoticon));
        }
        // Else, add to star
        else {
            // Save to database
            Favorite savedFavorite
                    = new Favorite(emoticon, description, "");
            savedFavorite.save();
            // Update cache
            mEmoticonInFavoritesCache.put(emoticon, true);
            // Update this star
            viewHolder.favorite.setImageDrawable(mStarDrawable);
            // Update other stars
            notifyDataSetChanged();
            // Notify main activity
            EventBus.getDefault().post(new FavoriteAddedEvent(emoticon));
        }
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        // Standard view holder pattern
        final SectionHeaderViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_section_header, parent, false);
            viewHolder = new SectionHeaderViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SectionHeaderViewHolder) convertView.getTag();
        }

        final Category category = mSource.getCategories().get(section);
        viewHolder.text.setText(category.getName());

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return mSectionHeaders;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionIndexToPositionMapping.get(sectionIndex);
    }

    @Subscribe
    public void handle(EmptyEvent event) {

    }

    static class EntryViewHolder {
        @Bind(R.id.root)
        RelativeLayout root;
        @Bind(R.id.emoticonTextView)
        TextView emoticon;
        @Bind(R.id.descriptionTextView)
        TextView description;
        @Bind(R.id.entryStarImageView)
        ImageView favorite;

        EntryViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class SectionHeaderViewHolder {
        @Bind(R.id.sectionHeaderTextView)
        TextView text;

        SectionHeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
