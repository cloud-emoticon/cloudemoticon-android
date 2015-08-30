package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.disk.Repository;

/**
 * A repository is removed from database
 */
public class RepositoryDeletedEvent {
    private Repository mRepository;

    public RepositoryDeletedEvent(Repository repository) {
        this.mRepository = repository;
    }

    public Repository getRepository() {
        return mRepository;
    }
}
