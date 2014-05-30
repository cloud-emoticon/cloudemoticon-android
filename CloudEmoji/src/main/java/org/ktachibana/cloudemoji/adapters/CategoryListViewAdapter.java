package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.EmoticonCopiedEvent;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.Favorite;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class CategoryListViewAdapter extends BaseAdapter implements View.OnClickListener, Constants {

    // SQL clauses
    private static final String FIND_BY_EMOTICON = "emoticon = ? ";
    // Constant drawables
    Drawable mNoStarDrawable;
    Drawable mStarDrawable;
    private Context mContext;
    private List<Entry> mCategory;
    private List<Favorite> mFavorites;

    public CategoryListViewAdapter(Context context, List<Entry> category) {
        this.mContext = context;
        this.mCategory = category;
        mFavorites = Favorite.listAll(Favorite.class);
        mNoStarDrawable = mContext.getResources().getDrawable(R.drawable.ic_unfavorite);
        mStarDrawable = mContext.getResources().getDrawable(R.drawable.ic_favorite);
    }

    @Override
    public int getCount() {
        return mCategory.size();
    }

    @Override
    public Object getItem(int i) {
        return mCategory.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Standard view holder pattern
        final ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater
                    = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_entry, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Entry entry = mCategory.get(i);

        // Setup emoticon
        viewHolder.emoticon.setText(entry.getEmoticon());

        // If no description, then hide it, else set it
        boolean hasDescription = entry.getDescription().equals("");
        if (hasDescription)
            viewHolder.description.setVisibility(View.GONE);
        else
            viewHolder.description.setText(entry.getDescription());

        // Setup star
        // TODO: this is laggy!
        boolean isStared = false;
        for (Favorite favorite : mFavorites) {
            if (favorite.getEmoticon().equals(entry.getEmoticon())) {
                isStared = true;
                break;
            }
        }
        viewHolder.favorite.setImageDrawable(isStared ? mStarDrawable : mNoStarDrawable);

        // Setup click listener
        viewHolder.emoticon.setOnClickListener(this);
        viewHolder.emoticon.setTag(entry.getEmoticon());
        viewHolder.description.setOnClickListener(this);
        viewHolder.description.setTag(entry.getEmoticon());
        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Favorite> findFavorite = Favorite.find(
                        Favorite.class,
                        FIND_BY_EMOTICON,
                        new String[]{entry.getEmoticon()});

                // If finds anything, delete it
                if (findFavorite.size() != 0) {
                    findFavorite.get(0).delete();
                    viewHolder.favorite.setImageDrawable(mNoStarDrawable);
                }

                // Else add to db
                else {
                    Favorite savedFavorite
                            = new Favorite(mContext, entry.getEmoticon(), entry.getDescription());
                    savedFavorite.save();
                    viewHolder.favorite.setImageDrawable(mStarDrawable);
                }
            }
        });
        return view;
    }

    // TODO: too hacky!
    @Override
    public void onClick(View view) {
        // If it is the text view containing emoticon pressed
        if (view instanceof TextView) {
            if (view.getTag() != null) {
                try {
                    // Get the string and tell anybody who cared about an emoticon being copied
                    String string = (String) view.getTag();
                    EventBus.getDefault().post(new EmoticonCopiedEvent(string));
                } catch (ClassCastException e) {
                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                }
            }
        }
    }

    static class ViewHolder {
        @InjectView(R.id.entryEmoticonTextView)
        TextView emoticon;
        @InjectView(R.id.entryDescriptionTextView)
        TextView description;
        @InjectView(R.id.entryFavoriteImageView)
        ImageView favorite;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}