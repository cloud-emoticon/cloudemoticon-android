package org.ktachibana.cloudemoji.sync.parse;

import com.parse.ParseUser;

import org.ktachibana.cloudemoji.sync.interfaces.User;

import java.util.Map;

import bolts.Task;

/**
 * Parse implementation of User interface
 */
public class ParseUserImplementation implements User {
    private ParseUser mInternalParseUser;
    private String mLocalPassword;

    public ParseUserImplementation() {
        mInternalParseUser = new ParseUser();
    }

    public ParseUserImplementation(String username, String password, String email) {
        mLocalPassword = password;
        mInternalParseUser = new ParseUser();
        mInternalParseUser.setUsername(username);
        mInternalParseUser.setPassword(password);
        mInternalParseUser.setEmail(email);
    }

    @Override
    public String getUsername() {
        return mInternalParseUser.getUsername();
    }

    @Override
    public void setUsername(String username) {
        mInternalParseUser.setUsername(username);
    }

    @Override
    public String getPassword() {
        return mLocalPassword;
    }

    @Override
    public void setPassword(String password) {
        mLocalPassword = password;
        mInternalParseUser.setPassword(password);
    }

    @Override
    public String getEmail() {
        return mInternalParseUser.getEmail();
    }

    @Override
    public void setEmail(String email) {
        mInternalParseUser.setEmail(email);
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
