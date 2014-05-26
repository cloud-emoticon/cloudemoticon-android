package org.ktachibana.cloudemoji.events;

/**
 * A category list item is clicked
 */
public class CategoryClickedEvent {
    private int mIndex;

    public CategoryClickedEvent(int index) {
        this.mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }
}
