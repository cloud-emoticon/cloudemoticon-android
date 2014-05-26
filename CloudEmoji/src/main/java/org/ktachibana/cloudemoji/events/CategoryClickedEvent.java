package org.ktachibana.cloudemoji.events;

public class CategoryClickedEvent {
    private int mIndex;

    public CategoryClickedEvent(int index) {
        this.mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }
}
