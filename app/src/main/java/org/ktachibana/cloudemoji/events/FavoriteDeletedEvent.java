package org.ktachibana.cloudemoji.events;

/**
 * Favorite deleted event
 */
public class FavoriteDeletedEvent {
    private String mEmoticon;

    public FavoriteDeletedEvent(String emoticon) {
        this.mEmoticon = emoticon;
    }

    public String getEmoticon() {
        return mEmoticon;
    }
}
