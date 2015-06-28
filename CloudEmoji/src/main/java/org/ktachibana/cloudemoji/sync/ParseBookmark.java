package org.ktachibana.cloudemoji.sync;

import com.parse.ParseClassName;
import com.parse.ParseObject;
    
@ParseClassName("Bookmark")
public class ParseBookmark extends ParseObject {
    public static final String KEY_FOR_EMOTICON = "emoticon";
    public static final String KEY_FOR_DESCRIPTION = "description";
    public static final String KEY_FOR_SHORTCUT = "shortcut";

    public String getEmoticon() {
        return getString(KEY_FOR_EMOTICON);
    }

    public void setEmoticon(String emoticon) {
        put(KEY_FOR_EMOTICON, emoticon);
    }

    public String getDescription() {
        return getString(KEY_FOR_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_FOR_DESCRIPTION, description);
    }

    public String getShortcut() {
        return getString(KEY_FOR_SHORTCUT);
    }

    public void setShortcut(String shortcut) {
        put(KEY_FOR_SHORTCUT, shortcut);
    }
}
