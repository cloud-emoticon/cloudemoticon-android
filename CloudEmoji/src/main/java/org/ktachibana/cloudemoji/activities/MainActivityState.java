package org.ktachibana.cloudemoji.activities;

import android.os.Parcel;
import android.os.Parcelable;

import org.ktachibana.cloudemoji.models.Source;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

import java.util.ArrayList;
import java.util.List;

public class MainActivityState implements Parcelable {
    private long repositoryId;
    private Source source;
    private SourceInMemoryCache sourceCache;
    private List<MainActivityDrawerItem> drawerItems;

    public MainActivityState(
            long repositoryId,
            Source source,
            SourceInMemoryCache sourceCache) {
        this.repositoryId = repositoryId;
        this.source = source;
        this.sourceCache = sourceCache;
        this.drawerItems = new ArrayList<>();
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
        this.source = sourceCache.get(repositoryId);
    }

    public Source getSource() {
        return source;
    }

    public SourceInMemoryCache getSourceCache() {
        return sourceCache;
    }

    public void addToDrawerItems(MainActivityDrawerItem item) {
        drawerItems.add(item);
    }

    public MainActivityDrawerItem getDrawerItem(int i) {
        return drawerItems.get(i);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.repositoryId);
        dest.writeParcelable(this.source, 0);
        dest.writeParcelable(this.sourceCache, 0);
        dest.writeTypedList(drawerItems);
    }

    private MainActivityState(Parcel in) {
        this.repositoryId = in.readLong();
        this.source = in.readParcelable(Source.class.getClassLoader());
        this.sourceCache = in.readParcelable(SourceInMemoryCache.class.getClassLoader());
        in.readTypedList(drawerItems, MainActivityDrawerItem.CREATOR);
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
