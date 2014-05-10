package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Repository;

public class RepositoryDeletedEvent {
    private Repository repository;

    public RepositoryDeletedEvent(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }
}
