package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Source;

/**
 * A repository is read and parsed to source
 */
public class RemoteRepositoryParsedEvent {
    private Source mSource;
    private long mId;

    public RemoteRepositoryParsedEvent(Source source, long id) {
        this.mSource = source;
        this.mId = id;
    }

    public Source getSource() {
        return mSource;
    }

    public long getId() {
        return mId;
    }
}
