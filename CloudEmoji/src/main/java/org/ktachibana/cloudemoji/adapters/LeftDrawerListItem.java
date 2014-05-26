package org.ktachibana.cloudemoji.adapters;

import org.ktachibana.cloudemoji.Constants;

/**
 * It is an abstraction for a list item on left drawer
 */
public class LeftDrawerListItem implements Constants {
    // The text on this list item
    private String mText;

    // The drawable on this list item
    private int mDrawable;

    // The id for reference in case it is clicked
    private long mId;

    public LeftDrawerListItem(String text, int drawable, long id) {
        this.mText = text;
        this.mDrawable = drawable;
        this.mId = id;
    }

    public String getText() {
        return mText;
    }

    public int getDrawable() {
        return mDrawable;
    }

    public long getId() {
        return mId;
    }
}
