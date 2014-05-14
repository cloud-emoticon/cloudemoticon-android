package org.ktachibana.cloudemoji.events;

public class StringCopiedEvent {
    private String string;

    public StringCopiedEvent(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
