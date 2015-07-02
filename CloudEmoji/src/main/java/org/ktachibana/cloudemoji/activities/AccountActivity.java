package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.auth.ParseUserState;
import org.ktachibana.cloudemoji.events.UserLoggedInEvent;
import org.ktachibana.cloudemoji.events.UserLoggedOutEvent;
import org.ktachibana.cloudemoji.fragments.AccountLogInOrRegisterFragment;
import org.ktachibana.cloudemoji.fragments.AccountUserProfileFragment;
import org.ktachibana.cloudemoji.sync.ParseBookmarkManager;
import org.ktachibana.cloudemoji.utils.NonCancelableProgressMaterialDialogBuilder;

import bolts.Continuation;
import bolts.Task;
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
        handleFirstLogInConflict();
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

    private void handleFirstLogInConflict() {
        final MaterialDialog dialog = new NonCancelableProgressMaterialDialogBuilder(this)
                .title(R.string.please_wait)
                .content(R.string.handling_first_login_conflict)
                .show();

        ParseBookmarkManager.handleFirstLoginConflict().continueWith(new Continuation<ParseBookmarkManager.FirstLoginConflictResult, Void>() {
            @Override
            public Void then(Task<ParseBookmarkManager.FirstLoginConflictResult> task) throws Exception {
                if (task.getResult() != ParseBookmarkManager.FirstLoginConflictResult.DIFFERENT) {
                    dialog.dismiss();
                } else {
                    Log.e("233", "waiting for merge");
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }
}