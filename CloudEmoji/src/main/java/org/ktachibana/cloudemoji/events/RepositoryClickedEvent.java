package org.ktachibana.cloudemoji.events;

public class RepositoryClickedEvent {
    private long id;

    public RepositoryClickedEvent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
