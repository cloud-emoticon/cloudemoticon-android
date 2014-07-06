package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.models.Category;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.Favorite;
import org.ktachibana.cloudemoji.models.Source;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

public class SourceListViewAdapter extends SectionedBaseAdapter implements SectionIndexer {
    // Constant drawables
    Drawable mNoStarDrawable;
    Drawable mStarDrawable;
    private Source mSource;
    private Context mContext;
    private LayoutInflater mInflater;
    // Save whether an emoticon is in favorite beforehand
    private HashMap<String, Boolean> mFavoriteByEmoticonCache;
    private String[] mCategoryNameSectionIndexer;

    public SourceListViewAdapter(Context context, Source source) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSource = source;
        mFavoriteByEmoticonCache = new LinkedHashMap<String, Boolean>();

        // Constant drawables
        mNoStarDrawable = mContext.getResources().getDrawable(R.drawable.ic_unfavorite);
        mStarDrawable = mContext.getResources().getDrawable(R.drawable.ic_favorite);

        // Setup emoticon to favorite cache
        List<Entry> entries = mSource.getAllEntries();
        for (Entry entry : entries) {
            String emoticon = entry.getEmoticon();
            mFavoriteByEmoticonCache.put(
                    emoticon,
                    Favorite.queryByEmoticon(emoticon).size() != 0);
        }

        mCategoryNameSectionIndexer = new String[mSource.getCategories().size()];
        for (int i = 0; i < mCategoryNameSectionIndexer.length; i++) {
            mCategoryNameSectionIndexer[i] = mSource.getCategories().get(i).getName();
        }
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
        viewHolder.emoticon.setText(entry.getEmoticon());

        // Set description GONE if no description
        boolean hasDescription = entry.getDescription().equals("");
        if (hasDescription) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(entry.getDescription());
        }

        // Setup star
        final boolean isStared = mFavoriteByEmoticonCache.get(entry.getEmoticon());
        viewHolder.favorite.setImageDrawable(isStared ? mStarDrawable : mNoStarDrawable);

        // Setup on star clicked
        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If already in favorite, remove it from favorites
                if (isStared) {
                    // Remove from database
                    List<Favorite> favoritesFound = Favorite.queryByEmoticon(entry.getEmoticon());
                    for (Favorite favorite : favoritesFound) {
                        favorite.delete();
                    }
                    // Update cache
                    mFavoriteByEmoticonCache.put(entry.getEmoticon(), false);
                    // Update this star
                    viewHolder.favorite.setImageDrawable(mNoStarDrawable);
                    // Update other stars
                    notifyDataSetChanged();
                    // Notify main activity
                    EventBus.getDefault().post(new FavoriteDeletedEvent(entry.getEmoticon()));
                }
                // Else, add to star
                else {
                    // Save to database
                    Favorite savedFavorite
                            = new Favorite(mContext, entry.getEmoticon(), entry.getDescription());
                    savedFavorite.save();
                    // Update cache
                    mFavoriteByEmoticonCache.put(entry.getEmoticon(), true);
                    // Update this star
                    viewHolder.favorite.setImageDrawable(mStarDrawable);
                    // Update other stars
                    notifyDataSetChanged();
                    // Notify main activity
                    EventBus.getDefault().post(new FavoriteAddedEvent(entry.getEmoticon()));
                }
            }
        });

        return convertView;
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
        return mCategoryNameSectionIndexer;
    }

    static class EntryViewHolder {
        @InjectView(R.id.emoticonTextView)
        TextView emoticon;
        @InjectView(R.id.descriptionTextView)
        TextView description;
        @InjectView(R.id.entryStarImageView)
        ImageView favorite;

        EntryViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class SectionHeaderViewHolder {
        @InjectView(R.id.sectionHeaderTextView)
        TextView text;

        SectionHeaderViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
