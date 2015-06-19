package org.ktachibana.cloudemoji.sync.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.ktachibana.cloudemoji.sync.interfaces.Bookmark;

/**
 * Parse implementation of Bookmark interface
 */
@ParseClassName("Bookmark")
public class ParseBookmarkImplementation extends ParseObject implements Bookmark {
    public static final String KEY_FOR_EMOTICON = "emoticon";
    public static final String KEY_FOR_DESCRIPTION = "description";
    public static final String KEY_FOR_SHORTCUT = "shortcut";

    @Override
    public String getEmoticon() {
        return getString(KEY_FOR_EMOTICON);
    }

    @Override
    public void setEmoticon(String emoticon) {
        put(KEY_FOR_EMOTICON, emoticon);
    }

    @Override
    public String getDescription() {
        return getString(KEY_FOR_DESCRIPTION);
    }

    @Override
    public void setDescription(String description) {
        put(KEY_FOR_DESCRIPTION, description);
    }

    @Override
    public String getShortcut() {
        return getString(KEY_FOR_SHORTCUT);
    }

    @Override
    public void setShortcut(String shortcut) {
        put(KEY_FOR_SHORTCUT, shortcut);
    }
}
