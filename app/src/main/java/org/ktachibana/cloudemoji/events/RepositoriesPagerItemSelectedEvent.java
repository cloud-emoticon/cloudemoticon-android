package org.ktachibana.cloudemoji.events;

public class RepositoriesPagerItemSelectedEvent {
    public int getItem() {
        return item;
    }

    private int item;

    public RepositoriesPagerItemSelectedEvent(int item) {
        this.item = item;
    }
}
