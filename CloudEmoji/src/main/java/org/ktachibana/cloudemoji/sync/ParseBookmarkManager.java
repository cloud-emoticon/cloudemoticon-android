package org.ktachibana.cloudemoji.sync;

import org.ktachibana.cloudemoji.auth.ParseUserState;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.remote.ParseBookmark;

import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class ParseBookmarkManager {
    /**
     * Read all
     */
    public static Task<List<Favorite>> readAllBookmarksRemotely() {
        if (ParseUserState.isLoggedIn()) {
            return ParseBookmark.getQuery(ParseUserState.getLoggedInUser())
                    .findInBackground()
                    .continueWith(new Continuation<List<ParseBookmark>, List<Favorite>>() {
                        @Override
                        public List<Favorite> then(Task<List<ParseBookmark>> task) throws Exception {
                            if (task.getResult() != null) {
                                return Favorite.convert(task.getResult());
                            }
                            throw new ParseBookmarkNotFoundException();
                        }
                    });
        }
        return null;
    }
}
