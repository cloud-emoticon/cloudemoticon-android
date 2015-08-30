package org.ktachibana.cloudemoji.events;

/**
 * Repository download has failed
 */
public class RepositoryDownloadFailedEvent {
    private Throwable mThrowable;

    public RepositoryDownloadFailedEvent(Throwable throwable) {
        mThrowable = throwable;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
