package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Source;

/**
 * A repository is read and parsed to source
 */
public class RepositoryParsedEvent {
    private Source mSource;

    public RepositoryParsedEvent(Source source) {
        this.mSource = source;
    }

    public Source getSource() {
        return mSource;
    }
}
