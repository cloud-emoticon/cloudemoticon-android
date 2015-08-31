package org.ktachibana.cloudemoji.activities;

import android.os.Parcel;
import android.os.Parcelable;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

@org.parceler.Parcel
public class MainActivityState {
    int itemId;
    int previousItemId;
    SourceInMemoryCache sourceCache;

    public MainActivityState() { /*Required empty bean constructor*/ }

    public MainActivityState(SourceInMemoryCache sourceCache) {
        this.itemId = Constants.DEFAULT_LIST_ITEM_ID;
        this.previousItemId = Constants.DEFAULT_LIST_ITEM_ID;
        this.sourceCache = sourceCache;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int newItemId) {
        this.previousItemId = this.itemId;
        this.itemId = newItemId;
    }

    public void revertToPreviousId() {
        this.itemId = this.previousItemId;
    }

    public SourceInMemoryCache getSourceCache() {
        return sourceCache;
    }

    public void setSourceCache(SourceInMemoryCache cache) {
        sourceCache = null;
        sourceCache = cache;
    }
}
