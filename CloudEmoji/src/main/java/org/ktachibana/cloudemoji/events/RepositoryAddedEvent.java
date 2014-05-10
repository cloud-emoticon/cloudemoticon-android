package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Repository;

public class RepositoryAddedEvent {
    private Repository repository;

    public RepositoryAddedEvent(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }
}
