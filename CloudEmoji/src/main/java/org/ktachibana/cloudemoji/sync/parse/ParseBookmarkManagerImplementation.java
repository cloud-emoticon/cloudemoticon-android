package org.ktachibana.cloudemoji.sync.parse;

import org.ktachibana.cloudemoji.sync.interfaces.Bookmark;
import org.ktachibana.cloudemoji.sync.interfaces.BookmarkManager;

import java.util.List;

import bolts.Task;

public class ParseBookmarkManagerImplementation implements BookmarkManager {
    @Override
    public Task<Void> addNewBookmark(Bookmark newBookmark) {
        return null;
    }

    @Override
    public Task<List<Bookmark>> getAllBookmarks() {
        return null;
    }
}
