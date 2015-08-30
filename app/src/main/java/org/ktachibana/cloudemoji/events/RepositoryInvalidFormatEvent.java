package org.ktachibana.cloudemoji.events;

public class RepositoryInvalidFormatEvent {
    private String type;

    public RepositoryInvalidFormatEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
