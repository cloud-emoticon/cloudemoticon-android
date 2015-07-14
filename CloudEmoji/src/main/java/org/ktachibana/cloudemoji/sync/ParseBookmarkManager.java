package org.ktachibana.cloudemoji.sync;

import org.ktachibana.cloudemoji.auth.ParseUserState;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.remote.ParseBookmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                    // TODO
                    return null;
                }
                throw new ParseBookmarkNotFoundException();
            }
        });
    }

    public static MergeResult merge(final List<Favorite> local, final List<ParseBookmark> remote) {
        MergeResult result = new MergeResult();

        // If both lists are empty, do nothing
        if (local.size() == 0 && remote.size() == 0) {
        }

        // If both local and remote are identical, do nothing
        else if (Favorite.listEquals(local, remote)) {
        }

        // If only local is empty, pull all from remote to local
        else if (local.size() == 0 && remote.size() != 0) {
            result.localUnique.addAll(Favorite.convert(remote));
        }

        // If only remote is empty, push all from local to remote
        else if (local.size() != 0 && remote.size() == 0) {
            result.remoteUnique.addAll(ParseBookmark.convert(ParseUserState.getLoggedInUser(), local));
        }

        // Merge
        else {
            // Mapping of emoticon string to a wrapper
            // Wrapper contains the emoticon string's index in local and remote, and a state
            // The state represents whether the emoticon is local only, remote only, or in both
            HashMap<String, MergeInfoWrapper> occurrences = new LinkedHashMap<>();

            // First pass

            // Iterate through local
            for (int i = 0; i < local.size(); i++) {
                Favorite favorite = local.get(i);
                occurrences.put(favorite.getEmoticon(), new MergeInfoWrapper(MergeState.LOCAL_ONLY, i, -1));
            }

            // Iterate through remote, mark 0 if already exists
            for (int i = 0; i < remote.size(); i++) {
                ParseBookmark bookmark = remote.get(i);
                String emoticon = bookmark.getEmoticon();
                if (!occurrences.containsKey(emoticon))
                    occurrences.put(emoticon, new MergeInfoWrapper(MergeState.REMOTE_ONLY, -1, i));
                else {
                    MergeInfoWrapper newWrapper = occurrences.get(emoticon);
                    newWrapper.state = MergeState.BOTH;
                    newWrapper.indexInRemote = i;
                    occurrences.put(emoticon, newWrapper);
                }
            }

            // Second pass

            for (Map.Entry<String, MergeInfoWrapper> entry : occurrences.entrySet()) {
                MergeInfoWrapper wrapper = entry.getValue();
                MergeState state = wrapper.state;
                int indexInLocal = wrapper.indexInLocal;
                int indexInRemote = wrapper.indexInRemote;
                // If only in local, push it to remote
                if (state == MergeState.LOCAL_ONLY)
                    result.remoteUnique.add(
                            new ParseBookmark(ParseUserState.getLoggedInUser(), local.get(indexInLocal))
                    );

                    // If only in remote, pull it from local
                else if (state == MergeState.REMOTE_ONLY)
                    result.localUnique.add(
                            new Favorite(remote.get(indexInRemote))
                    );

                    // Otherwise in both, overwrite description/shortcut from local/remote that is more recently modified
                else {
                    // local and remote
                    Favorite localFavorite = local.get(indexInLocal);
                    ParseBookmark remoteBookmark = remote.get(indexInRemote);

                    // Compare last modified time
                    long localLastModifiedTime = localFavorite.getLastModifiedTime();
                    long remoteLastModifiedTime = remoteBookmark.getUpdatedAt().getTime();
                    boolean localIsMoreRecent = localLastModifiedTime > remoteLastModifiedTime;

                    // If local is more recent, update remote
                    if (localIsMoreRecent) {
                        remoteBookmark.setDescription(localFavorite.getDescription());
                        remoteBookmark.setShortcut(localFavorite.getShortcut());
                        result.remoteMerged.add(remoteBookmark);
                    }

                    // Else remote is more recent, update local
                    else {
                        localFavorite.setDescription(remoteBookmark.getDescription());
                        localFavorite.setShortcut(remoteBookmark.getShortcut());
                        result.localMerged.add(localFavorite);
                    }
                }
            }
        }

        return result;
    }

    private enum MergeState {
        LOCAL_ONLY, REMOTE_ONLY, BOTH
    }

    private static class MergeInfoWrapper {
        public MergeState state;
        public int indexInLocal;
        public int indexInRemote;

        public MergeInfoWrapper(MergeState state, int indexInLocal, int indexInRemote) {
            this.state = state;
            this.indexInLocal = indexInLocal;
            this.indexInRemote = indexInRemote;
        }
    }

    public static class MergeResult {
        // Contents that local needs to create
        public List<Favorite> localUnique;

        // Contents that remote needs to create
        public List<ParseBookmark> remoteUnique;

        // Contents that local needs to update
        public List<Favorite> localMerged;

        // Contents that remote needs to update
        public List<ParseBookmark> remoteMerged;

        public MergeResult() {
            this.localUnique = new ArrayList<>();
            this.remoteUnique = new ArrayList<>();
            this.localMerged = new ArrayList<>();
            this.remoteMerged = new ArrayList<>();
        }
    }
}
