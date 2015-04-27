package org.ktachibana.cloudemoji.activities;

import android.os.Parcel;
import android.os.Parcelable;

import org.ktachibana.cloudemoji.models.Source;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

public class MainActivityState implements Parcelable {
    private long repositoryId;
    private Source source;
    private SourceInMemoryCache sourceCache;

    public MainActivityState(
            long repositoryId,
            Source source,
            SourceInMemoryCache sourceCache) {
        this.repositoryId = repositoryId;
        this.source = source;
        this.sourceCache = sourceCache;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public SourceInMemoryCache getSourceCache() {
        return sourceCache;
    }

    public void setSourceCache(SourceInMemoryCache sourceCache) {
        this.sourceCache = sourceCache;
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
    }

    private MainActivityState(Parcel in) {
        this.repositoryId = in.readLong();
        this.source = in.readParcelable(Source.class.getClassLoader());
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
