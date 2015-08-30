package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.disk.Repository;

/**
 * A repository is added to database
 */
public class RepositoryAddedEvent {
    private Repository mRepository;

    public RepositoryAddedEvent(Repository repository) {
        this.mRepository = repository;
    }

    public Repository getRepository() {
        return mRepository;
    }
}
