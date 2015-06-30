package org.ktachibana.cloudemoji.models.memory;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO class holding a repository in store
 */
public class StoreRepository implements Parcelable {
    private String alias;
    private String url;
    private String description;
    private String author;
    private String authorUrl;
    private String authorIconUrl;

    public StoreRepository(
            String alias,
            String url,
            String description,
            String author,
            String authorUrl,
            String authorIconUrl
    ) {
        this.alias = alias;
        this.url = url;
        this.description = description;
        this.author = author;
        this.authorUrl = authorUrl;
        this.authorIconUrl = authorIconUrl;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getAuthorIconUrl() {
        return authorIconUrl;
    }

    public void setAuthorIconUrl(String authorIconUrl) {
        this.authorIconUrl = authorIconUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.alias);
        dest.writeString(this.url);
        dest.writeString(this.description);
        dest.writeString(this.author);
        dest.writeString(this.authorUrl);
        dest.writeString(this.authorIconUrl);
    }

    private StoreRepository(Parcel in) {
        this.alias = in.readString();
        this.url = in.readString();
        this.description = in.readString();
        this.author = in.readString();
        this.authorUrl = in.readString();
        this.authorIconUrl = in.readString();
    }

    public static final Parcelable.Creator<StoreRepository> CREATOR = new Parcelable.Creator<StoreRepository>() {
        public StoreRepository createFromParcel(Parcel source) {
            return new StoreRepository(source);
        }

        public StoreRepository[] newArray(int size) {
            return new StoreRepository[size];
        }
    };
}
