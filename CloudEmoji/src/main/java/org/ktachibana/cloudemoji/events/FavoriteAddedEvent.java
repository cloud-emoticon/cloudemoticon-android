package org.ktachibana.cloudemoji.events;

/**
 * Favorite deleted event
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
