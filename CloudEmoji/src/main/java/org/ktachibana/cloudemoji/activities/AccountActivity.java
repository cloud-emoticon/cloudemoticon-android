package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.UserLoggedInEvent;
import org.ktachibana.cloudemoji.events.UserLoggedOutEvent;
import org.ktachibana.cloudemoji.fragments.AccountLogInOrRegisterFragment;
import org.ktachibana.cloudemoji.fragments.AccountUserProfileFragment;
import org.ktachibana.cloudemoji.sync.Sync;

public class AccountActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if (Sync.getUserState().isLoggedIn()) {
            showUserProfile();
        } else {
            showLogInOrRegister();
        }
    }

    public void onEvent(UserLoggedInEvent event) {
        showUserProfile();
    }

    public void onEvent(UserLoggedOutEvent event) {
        showLogInOrRegister();
    }

    private void showLogInOrRegister() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root, new AccountLogInOrRegisterFragment())
                .commit();
    }

    private void showUserProfile() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root, new AccountUserProfileFragment())
                .commit();
    }
}