package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Repository;

/**
 * A repository is downloaded from Internet
 */
public class RepositoryDownloadedEvent {
    private Repository mRepository;
    private Exception mException;

    public RepositoryDownloadedEvent(Repository repository, Exception exception) {
        this.mRepository = repository;
        this.mException = exception;
    }

    public Repository getRepository() {
        return mRepository;
    }

    public Exception getException() {
        return mException;
    }
}
