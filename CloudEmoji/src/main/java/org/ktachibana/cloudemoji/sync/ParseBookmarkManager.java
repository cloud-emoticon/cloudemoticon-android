package org.ktachibana.cloudemoji.sync;

import org.ktachibana.cloudemoji.auth.ParseUserState;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.remote.ParseBookmark;

import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class ParseBookmarkManager {
    /**
     * Create one
     */
    public static void createBookmarkRemotely(Favorite favorite) {
        ParseBookmark bookmark = new ParseBookmark(ParseUserState.getLoggedInUser());
        bookmark.setEmoticon(favorite.getEmoticon());
        bookmark.setDescription(favorite.getDescription());
        bookmark.setShortcut(favorite.getShortcut());
        bookmark.saveEventually();
    }

    /**
     * Read all
     */
    public static Task<List<Favorite>> readAllBookmarksRemotely() {
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

    /**
     * Utils
     */

    /**
     * Try to handle first login conflict
     * If local contents are empty and remote contents are empty, do nothing
     * If EITHER local contents OR remote contents are non-empty, download/upload from one to another
     * If BOTH local contents AND remote contents are non-empty, try to merge
     */
    public static Task<FirstLoginConflictResult> handleFirstLoginConflict() {
        // Read local favorites
        final List<Favorite> local = Favorite.listAll(Favorite.class);

        // Read remote favorites
        return readAllBookmarksRemotely().continueWith(new Continuation<List<Favorite>, FirstLoginConflictResult>() {
            @Override
            public FirstLoginConflictResult then(Task<List<Favorite>> task) throws Exception {
                List<Favorite> remote = task.getResult();
                if (remote != null) {

                    // If both local and remote are empty then do nothing
                    if (local.size() == 0 && remote.size() == 0)
                        return FirstLoginConflictResult.BOTH_EMPTY;

                    // If local is non-empty and remote is empty then upload
                    if (local.size() != 0 && remote.size() == 0) {
                        for (Favorite favorite : local) {
                            createBookmarkRemotely(favorite);
                        }
                        return FirstLoginConflictResult.REMOTE_EMPTY;
                    }

                    // If local is empty and remote is non-empty then download
                    if (local.size() == 0 && remote.size() != 0) {
                        for (Favorite favorite : remote) {
                            favorite.save();
                        }
                        return FirstLoginConflictResult.LOCAL_EMPTY;
                    }

                    // If both are non-empty, check for identical
                    if (Favorite.listEquals(local, remote))
                        return FirstLoginConflictResult.IDENTICAL;

                    // Otherwise different
                    return FirstLoginConflictResult.DIFFERENT;
                }
                throw new ParseBookmarkNotFoundException();
            }
        });
    }

    public enum FirstLoginConflictResult {
        BOTH_EMPTY, LOCAL_EMPTY, REMOTE_EMPTY, IDENTICAL, DIFFERENT
    }
}
