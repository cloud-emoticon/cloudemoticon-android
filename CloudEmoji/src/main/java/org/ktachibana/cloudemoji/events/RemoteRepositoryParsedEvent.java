package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Source;

/**
 * Remote repository parsed event
 */
public class RemoteRepositoryParsedEvent {
    private long mId;
    private Source mSource;

    public RemoteRepositoryParsedEvent(long id, Source source) {
        this.mId = id;
        this.mSource = source;
    }

    public long getId() {
        return mId;
    }

    public Source getSource() {
        return mSource;
    }
}
