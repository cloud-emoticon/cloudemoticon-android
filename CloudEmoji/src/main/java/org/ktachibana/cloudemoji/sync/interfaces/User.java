package org.ktachibana.cloudemoji.sync.interfaces;

import java.util.Map;

import bolts.Task;

/**
 * A general interface that represents a user.
 * It is designed as an interface so that the detailed implementation can be swappable
 */
public interface User {
    String getUsername();
    void setUsername(String username);
    String getPassword();
    void setPassword(String password);
    String getEmail();
    void setEmail(String email);
    Map<String, String> getExtra();
    void setExtra(Map<String, String> extra);
    Task<Void> register();
}
