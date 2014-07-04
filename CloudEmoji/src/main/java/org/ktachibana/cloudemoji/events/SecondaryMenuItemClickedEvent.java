package org.ktachibana.cloudemoji.events;

public class SecondaryMenuItemClickedEvent {
    private long mId;

    public SecondaryMenuItemClickedEvent(long id) {
        this.mId = id;
    }

    public long getId() {
        return mId;
    }
}
