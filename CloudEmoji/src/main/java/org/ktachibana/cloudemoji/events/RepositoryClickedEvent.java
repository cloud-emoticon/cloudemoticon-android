package org.ktachibana.cloudemoji.events;

/**
 * A repository is clicked on left drawer
 */
public class RepositoryClickedEvent {
    private long mId;

    public RepositoryClickedEvent(long id) {
        this.mId = id;
    }

    public long getId() {
        return mId;
    }
}
