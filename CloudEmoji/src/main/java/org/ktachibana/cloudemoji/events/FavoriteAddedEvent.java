package org.ktachibana.cloudemoji.events;

/**
 * Favorite added event
 */
public class FavoriteAddedEvent {
    private String mEmoticon;

    public FavoriteAddedEvent(String emoticon) {
        this.mEmoticon = emoticon;
    }

    public String getEmoticon() {
        return mEmoticon;
    }
}
