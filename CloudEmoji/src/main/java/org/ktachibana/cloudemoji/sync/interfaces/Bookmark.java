package org.ktachibana.cloudemoji.sync.interfaces;

/**
 * A general interface that represents a bookmark.
 * It is designed as an interface so that the detailed implementation can be swappable
 */
public interface Bookmark {
    String getEmoticon();
    void setEmoticon(String emoticon);
    String getDescription();
    void setDescription(String description);
    String getShortcut();
    void setShortcut(String shortcut);
}
