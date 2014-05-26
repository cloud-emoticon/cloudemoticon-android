package org.ktachibana.cloudemoji.events;

/**
 * A local repository list item is clicked
 */
public class LocalRepositoryClickedEvent {
    private long mId;

    public LocalRepositoryClickedEvent(long id) {
        this.mId = id;
    }

    public long getId() {
        return mId;
    }
}
