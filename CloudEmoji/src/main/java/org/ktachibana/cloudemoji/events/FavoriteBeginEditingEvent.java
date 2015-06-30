package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.disk.Favorite;

/**
 * When favorite is being edited
 */
public class FavoriteBeginEditingEvent {
    private Favorite mFavorite;

    public FavoriteBeginEditingEvent(Favorite favorite) {
        mFavorite = favorite;
    }

    public Favorite getFavorite() {
        return mFavorite;
    }
}
