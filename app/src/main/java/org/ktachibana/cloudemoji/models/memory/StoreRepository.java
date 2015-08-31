package org.ktachibana.cloudemoji.models.memory;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO class holding a repository in store
 */
@org.parceler.Parcel
public class StoreRepository {
    String alias;
    String url;
    String description;
    String author;
    String authorUrl;
    String authorIconUrl;

    public StoreRepository() { /*Required empty bean constructor*/ }

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
}
