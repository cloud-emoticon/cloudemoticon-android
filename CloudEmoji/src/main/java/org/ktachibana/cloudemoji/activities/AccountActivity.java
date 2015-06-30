package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.auth.ParseUserState;
import org.ktachibana.cloudemoji.events.UserLoggedInEvent;
import org.ktachibana.cloudemoji.events.UserLoggedOutEvent;
import org.ktachibana.cloudemoji.fragments.AccountLogInOrRegisterFragment;
import org.ktachibana.cloudemoji.fragments.AccountUserProfileFragment;

import de.greenrobot.event.Subscribe;

public class AccountActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if (ParseUserState.isLoggedIn()) {
            showUserProfile();
        } else {
            showLogInOrRegister();
        }
    }

    @Subscribe
    public void handle(UserLoggedInEvent event) {
        showUserProfile();
    }

    @Subscribe
    public void handle(UserLoggedOutEvent event) {
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