package org.ktachibana.cloudemoji.activities;

import android.os.Parcel;
import android.os.Parcelable;

import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

public class MainActivityState implements Parcelable {
    private int itemId;
    private SourceInMemoryCache sourceCache;

    public MainActivityState(
            int itemId,
            SourceInMemoryCache sourceCache) {
        this.itemId = itemId;
        this.sourceCache = sourceCache;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public SourceInMemoryCache getSourceCache() {
        return sourceCache;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.itemId);
        dest.writeParcelable(this.sourceCache, 0);
    }

    private MainActivityState(Parcel in) {
        this.itemId = in.readInt();
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
