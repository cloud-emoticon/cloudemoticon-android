package org.ktachibana.cloudemoji.sync.interfaces;

import java.util.List;

import bolts.Task;

/**
 * A general interface that represents a Bookmark manager.
 * It is designed as an interface so that the detailed implementation can be swappable
 */
public interface BookmarkManager {
    Task<Void> addNewBookmark(Bookmark newBookmark);
    Task<List<Bookmark>> getAllBookmarks();
}
