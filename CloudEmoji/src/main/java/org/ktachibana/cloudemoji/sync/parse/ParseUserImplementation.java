package org.ktachibana.cloudemoji.sync.parse;

import com.parse.ParseUser;

import org.ktachibana.cloudemoji.sync.interfaces.User;

import java.util.Map;

import bolts.Task;

/**
 * Parse implementation of User interface
 */
public class ParseUserImplementation implements User {
    private String username;
    private String password;
    private String email;
    private ParseUser mInternalParseUser;

    public ParseUserImplementation() {

    }

    public ParseUserImplementation(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        mInternalParseUser = new ParseUser();
        mInternalParseUser.setUsername(username);
        mInternalParseUser.setPassword(password);
        mInternalParseUser.setEmail(email);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Map<String, String> getExtra() {
        throw new UnsupportedOperationException("getExtra() is not implemented");
    }

    @Override
    public void setExtra(Map<String, String> extra) {
        throw new UnsupportedOperationException("setExtra() is not implemented");
    }

    @Override
    public Task<Void> register() {
        return mInternalParseUser.signUpInBackground();
    }
}
