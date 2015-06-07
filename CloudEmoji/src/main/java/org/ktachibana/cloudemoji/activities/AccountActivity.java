package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.fragments.AccountLogInOrRegisterFragment;
import org.ktachibana.cloudemoji.fragments.AccountUserProfileFragment;
import org.ktachibana.cloudemoji.sync.Sync;

public class AccountActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if (Sync.getUserState().isLoggedIn()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root, new AccountUserProfileFragment())
                    .commit();
        }
        else
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root, new AccountLogInOrRegisterFragment())
                    .commit();
        }
    }
}