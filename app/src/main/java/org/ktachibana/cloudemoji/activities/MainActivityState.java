package org.ktachibana.cloudemoji.activities;

import android.os.Parcel;
import android.os.Parcelable;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

public class MainActivityState implements Parcelable, Constants {
    private int itemId;
    private int previousItemId;
    private SourceInMemoryCache sourceCache;

    public MainActivityState(SourceInMemoryCache sourceCache) {
        this.itemId = DEFAULT_LIST_ITEM_ID;
        this.previousItemId = DEFAULT_LIST_ITEM_ID;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.itemId);
        dest.writeInt(this.previousItemId);
        dest.writeParcelable(this.sourceCache, 0);
    }

    private MainActivityState(Parcel in) {
        this.itemId = in.readInt();
        this.previousItemId = in.readInt();
        this.sourceCache = in.readParcelable(SourceInMemoryCache.class.getClassLoader());
    }

    public static final Parcelable.Creator<MainActivityState> CREATOR = new Parcelable.Creator<MainActivityState>() {
        public MainActivityState createFromParcel(Parcel source) {
            return new MainActivityState(source);
        }

        public MainActivityState[] newArray(int size) {
            return new MainActivityState[size];
        }
    };
}
