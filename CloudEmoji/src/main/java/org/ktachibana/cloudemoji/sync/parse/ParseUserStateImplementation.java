package org.ktachibana.cloudemoji.sync.parse;

import com.parse.ParseUser;

import org.ktachibana.cloudemoji.sync.interfaces.User;
import org.ktachibana.cloudemoji.sync.interfaces.UserState;

import bolts.Task;

/**
 * Parse implementation of UserState interface
 */
public class ParseUserStateImplementation implements UserState {
    @Override
    public Task<Void> login(User user) {
        Task<ParseUser> task = ParseUser.logInInBackground(
                user.getUsername(),
                user.getPassword()
        );
        return task.makeVoid();
    }

    @Override
    public Task<Void> logout() {
        return ParseUser.logOutInBackground();
    }

    @Override
    public boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }

    @Override
    public User getLoggedInUser() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        return new ParseUserImplementation(
                parseUser.getUsername(),
                null,
                parseUser.getEmail()
        );
    }
}
