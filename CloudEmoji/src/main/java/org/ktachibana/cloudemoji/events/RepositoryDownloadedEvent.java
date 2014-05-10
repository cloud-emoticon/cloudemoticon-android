package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Repository;

public class RepositoryDownloadedEvent {
    private Repository repository;
    private Status status;

    public RepositoryDownloadedEvent(Repository repository, Status status) {
        this.repository = repository;
        this.status = status;
    }

    public Repository getRepository() {
        return repository;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status{SUCCESS, FAIL}
}
