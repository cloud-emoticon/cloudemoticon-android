package org.ktachibana.cloudemoji.auth;

import com.parse.ParseUser;

public class ParseUserState {
    public static boolean isLoggedIn() {
        return getLoggedInUser() != null;
    }

    public static ParseUser getLoggedInUser() {
        return ParseUser.getCurrentUser();
    }
}
