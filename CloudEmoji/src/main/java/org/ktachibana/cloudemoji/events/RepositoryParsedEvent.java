package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Source;

public class RepositoryParsedEvent {
    private Source source;

    public RepositoryParsedEvent(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }
}
