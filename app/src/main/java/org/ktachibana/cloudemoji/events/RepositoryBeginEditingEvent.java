package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.disk.Repository;

/**
 * When a repository is being edited
 */
public class RepositoryBeginEditingEvent {
    private Repository mRepository;

    public RepositoryBeginEditingEvent(Repository repository) {
        this.mRepository = repository;
    }

    public Repository getRepository() {
        return mRepository;
    }
}
