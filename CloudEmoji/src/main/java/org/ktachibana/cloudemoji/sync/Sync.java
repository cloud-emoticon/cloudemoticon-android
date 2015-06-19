package org.ktachibana.cloudemoji.sync;

import org.ktachibana.cloudemoji.sync.interfaces.Bookmark;
import org.ktachibana.cloudemoji.sync.interfaces.User;
import org.ktachibana.cloudemoji.sync.interfaces.UserState;
import org.ktachibana.cloudemoji.sync.parse.ParseBookmarkImplementation;
import org.ktachibana.cloudemoji.sync.parse.ParseUserImplementation;
import org.ktachibana.cloudemoji.sync.parse.ParseUserStateImplementation;

public class Sync {
    public static User getUser() {
        return new ParseUserImplementation();
    }

    public static UserState getUserState() {
        return new ParseUserStateImplementation();
    }

    public static Bookmark getBookmark() {
        return new ParseBookmarkImplementation();
    }
}
