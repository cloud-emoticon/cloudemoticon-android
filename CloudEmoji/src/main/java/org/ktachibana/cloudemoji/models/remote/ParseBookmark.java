package org.ktachibana.cloudemoji.models.remote;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Bookmark")
public class ParseBookmark extends ParseObject {
    private static final String KEY_FOR_EMOTICON = "emoticon";
    private static final String KEY_FOR_DESCRIPTION = "description";
    private static final String KEY_FOR_SHORTCUT = "shortcut";
    private static final String KEY_FOR_OWNER = "owner";

    public ParseBookmark() {
    }

    public ParseBookmark(ParseUser user) {
        setACL(new ParseACL(user));
        put(KEY_FOR_OWNER, user);
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

    public static ParseQuery<ParseBookmark> getQuery(ParseUser user) {
        ParseQuery<ParseBookmark> query = ParseQuery.getQuery(ParseBookmark.class);
        query.whereEqualTo(KEY_FOR_OWNER, user);
        return query;
    }
}
