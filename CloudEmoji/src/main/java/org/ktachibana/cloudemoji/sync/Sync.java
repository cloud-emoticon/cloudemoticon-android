package org.ktachibana.cloudemoji.sync;

import org.ktachibana.cloudemoji.sync.interfaces.User;
import org.ktachibana.cloudemoji.sync.interfaces.UserState;
import org.ktachibana.cloudemoji.sync.parse.ParseUserImplementation;
import org.ktachibana.cloudemoji.sync.parse.ParseUserStateImplementation;

public class Sync {
    public static User getUser() {
        return new ParseUserImplementation();
    }

    public static UserState getUserState() {
        return new ParseUserStateImplementation();
    }
}
