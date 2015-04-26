package org.ktachibana.cloudemoji.events;

public class RepositoryExportedEvent {
    private String path;

    public RepositoryExportedEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
