package org.ktachibana.cloudemoji.events;

public class RepositoryExportEvent {
    private String json;
    private String alias;

    public RepositoryExportEvent(String json, String alias) {
        this.json = json;
        this.alias = alias;
    }

    public String getJson() {
        return json;
    }

    public String getAlias() {
        return alias;
    }
}
