package org.ktachibana.cloudemoji.events;

/**
 * Remote repository list item being clicked
 */
public class RemoteRepositoryClickedEvent {
    private long mId;

    public RemoteRepositoryClickedEvent(long id) {
        this.mId = id;
    }

    public long getId() {
        return mId;
    }
}
