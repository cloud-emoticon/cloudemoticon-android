package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Repository;

public class RepositoryDownloadedEvent {
    private Repository repository;
    private Exception exception;

    public RepositoryDownloadedEvent(Repository repository, Exception exception) {
        this.repository = repository;
        this.exception = exception;
    }

    public Repository getRepository() {
        return repository;
    }

    public Exception getException() {
        return exception;
    }
}
