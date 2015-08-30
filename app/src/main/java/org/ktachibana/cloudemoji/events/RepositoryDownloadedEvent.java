package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.disk.Repository;

/**
 * A repository is downloaded from Internet
 */
public class RepositoryDownloadedEvent {
    private Repository mRepository;

    public RepositoryDownloadedEvent(Repository repository) {
        this.mRepository = repository;
    }

    public Repository getRepository() {
        return mRepository;
    }
}
