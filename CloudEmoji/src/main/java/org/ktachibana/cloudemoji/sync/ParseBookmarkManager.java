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
    public static void createBookmark(
            String emoticon,
            String description,
            String shortcut) {
        Favorite favorite = new Favorite(emoticon, description, shortcut);
        createBookmarkLocally(favorite);
        if (ParseUserState.isLoggedIn()) {
            createBookmarkRemotely(favorite);
        }
    }

    public static void createBookmarkLocally(Favorite favorite) {
        favorite.save();
    }

    public static void createBookmarkRemotely(Favorite favorite) {
        ParseBookmark bookmark = new ParseBookmark(ParseUserState.getLoggedInUser(), favorite);
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
     * Update one
     */
    public static void updateBookmarkRemotely(final Favorite favorite) {
        ParseBookmark.getQuery(ParseUserState.getLoggedInUser(), favorite.getEmoticon())
                .getFirstInBackground()
                .continueWith(new Continuation<ParseBookmark, Void>() {
                    @Override
                    public Void then(Task<ParseBookmark> task) throws Exception {
                        if (task.getResult() != null) {
                            ParseBookmark remote = task.getResult();
                            remote.setDescription(favorite.getDescription());
                            remote.setShortcut(favorite.getShortcut());
                            remote.saveEventually();
                            return null;
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
                final List<Favorite> remote = task.getResult();
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
                    handleFirstLoginConflictWithDifference(local, remote);
                    return FirstLoginConflictResult.DIFFERENT;
                }
                throw new ParseBookmarkNotFoundException();
            }
        });
    }

    private static void handleFirstLoginConflictWithDifference(List<Favorite> local, List<Favorite> remote) {
        // TODO: remove result enum and use this code only
    }

    public enum FirstLoginConflictResult {
        BOTH_EMPTY, LOCAL_EMPTY, REMOTE_EMPTY, IDENTICAL, DIFFERENT
    }
}
