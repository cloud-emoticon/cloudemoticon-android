package org.ktachibana.cloudemoji.sync.interfaces;

import bolts.Task;

/**
 * A general interface to manage the state of current user
 */
public interface UserState {
    Task<Void> login(User user);
    Task<Void> logout();
    boolean isLoggedIn();
    User getLoggedInUser();
}
