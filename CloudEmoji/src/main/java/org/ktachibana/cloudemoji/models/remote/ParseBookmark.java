package org.ktachibana.cloudemoji.models.remote;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.ktachibana.cloudemoji.models.disk.Favorite;

@ParseClassName("Bookmark")
public class ParseBookmark extends ParseObject {
    private static final String KEY_FOR_EMOTICON = "emoticon";
    private static final String KEY_FOR_DESCRIPTION = "description";
    private static final String KEY_FOR_SHORTCUT = "shortcut";
    private static final String KEY_FOR_OWNER = "owner";

    public ParseBookmark() {
    }

    public ParseBookmark(ParseUser owner) {
        setACL(new ParseACL(owner));
        put(KEY_FOR_OWNER, owner);
    }

    public ParseBookmark(ParseUser owner, Favorite favorite) {
        this(owner);
        setEmoticon(favorite.getEmoticon());
        setDescription(favorite.getDescription());
        setShortcut(favorite.getShortcut());
    }

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

    public static ParseQuery<ParseBookmark> getQuery(ParseUser owner) {
        ParseQuery<ParseBookmark> query = ParseQuery.getQuery(ParseBookmark.class);
        query.whereEqualTo(KEY_FOR_OWNER, owner);
        return query;
    }

    public static ParseQuery<ParseBookmark> getQuery(ParseUser owner, String emoticon) {
        ParseQuery<ParseBookmark> query = getQuery(owner);
        query.whereEqualTo(KEY_FOR_EMOTICON, emoticon);
        return query;
    }
}
