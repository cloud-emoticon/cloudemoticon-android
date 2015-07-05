package org.ktachibana.cloudemoji.sync;

import org.ktachibana.cloudemoji.auth.ParseUserState;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.remote.ParseBookmark;

import java.util.ArrayList;
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

    private static void createBookmarkLocally(Favorite favorite) {
        favorite.save();
    }

    private static void createBookmarkRemotely(Favorite favorite) {
        ParseBookmark bookmark = new ParseBookmark(ParseUserState.getLoggedInUser(), favorite);
        bookmark.saveEventually();
    }

    /**
     * Read all
     */
    // TODO: read all transparently
    public static List<Favorite> readAllBookmarksLocally() {
        return Favorite.listAll(Favorite.class);
    }

    public static Task<List<ParseBookmark>> readAllBookmarksRemotely() {
        return ParseBookmark.getQuery(ParseUserState.getLoggedInUser())
                .findInBackground();
    }

    /**
     * Update one
     */
    public static void updateBookmark(
            String emoticon,
            String description,
            String shortcut) {
        Favorite favorite = Favorite.queryByEmoticon(emoticon);
        if (favorite != null) {
            favorite.setDescription(description);
            favorite.setShortcut(shortcut);
            updateBookmarkLocally(favorite);
            if (ParseUserState.isLoggedIn()) {
                updateBookmarkRemotely(favorite);
            }
        }
    }

    private static void updateBookmarkLocally(Favorite favorite) {
        favorite.save();
    }

    private static void updateBookmarkRemotely(final Favorite favorite) {
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
     * Delete one
     */
    public static void deleteBookmark(String emoticon) {
        Favorite favorite = Favorite.queryByEmoticon(emoticon);
        deleteBookmarkLocally(favorite);
        if (ParseUserState.isLoggedIn()) {
            deleteBookmarkRemotely(favorite);
        }
    }

    private static void deleteBookmarkLocally(Favorite favorite) {
        favorite.delete();
    }

    private static void deleteBookmarkRemotely(Favorite favorite) {
        ParseBookmark.getQuery(ParseUserState.getLoggedInUser(), favorite.getEmoticon())
                .getFirstInBackground()
                .continueWith(new Continuation<ParseBookmark, Void>() {
                    @Override
                    public Void then(Task<ParseBookmark> task) throws Exception {
                        if (task.getResult() != null) {
                            task.getResult().deleteEventually();
                        }
                        throw new ParseBookmarkNotFoundException();
                    }
                });
    }

    /**
     * Utils
     */

    /**
     * Handle first login conflict
     * If local contents are empty and remote contents are empty, do nothing
     * If EITHER local contents OR remote contents are non-empty, download/upload from one to another
     * If BOTH local contents AND remote contents are non-empty, merge
     */
    public static Task<Void> handleFirstLoginConflict() {
        final List<Favorite> local = readAllBookmarksLocally();

        return readAllBookmarksRemotely().continueWith(new Continuation<List<ParseBookmark>, Void>() {
            @Override
            public Void then(Task<List<ParseBookmark>> task) throws Exception {
                List<ParseBookmark> remote = task.getResult();
                if (remote != null) {
                    MergeResult result = merge(local, remote);
                    return null;
                }
                throw new ParseBookmarkNotFoundException();
            }
        });
    }

    public static MergeResult merge(List<Favorite> local, List<ParseBookmark> remote) {
        MergeResult result = new MergeResult();
        List<Favorite> localUnique = new ArrayList<>();
        List<ParseBookmark> remoteUnique = new ArrayList<>();
        List<Favorite> localMerged = new ArrayList<>();
        List<ParseBookmark> remoteMerged = new ArrayList<>();

        // TODO

        result.localUnique = localUnique;
        result.remoteUnique = remoteUnique;
        result.localMerged = localMerged;
        result.remoteMerged = remoteMerged;
        return result;
    }

    public static class MergeResult {
        public List<Favorite> localUnique;
        public List<ParseBookmark> remoteUnique;
        public List<Favorite> localMerged;
        public List<ParseBookmark> remoteMerged;
    }
}
