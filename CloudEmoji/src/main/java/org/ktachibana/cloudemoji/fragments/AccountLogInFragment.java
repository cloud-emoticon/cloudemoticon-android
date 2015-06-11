package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.UserLoggedInEvent;
import org.ktachibana.cloudemoji.sync.Sync;
import org.ktachibana.cloudemoji.sync.interfaces.User;
import org.ktachibana.cloudemoji.sync.interfaces.UserState;
import org.ktachibana.cloudemoji.utils.CredentialsValidator;
import org.ktachibana.cloudemoji.utils.Termination;
import org.ktachibana.cloudemoji.utils.NonCancelableProgressMaterialDialogBuilder;

import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class AccountLogInFragment extends BaseFragment {
    @InjectView(R.id.username)
    EditText usernameEditText;
    @InjectView(R.id.password)
    EditText passwordEditText;
    @InjectView(R.id.log_in)
    Button logInButton;

    public AccountLogInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_log_in, container, false);
        ButterKnife.inject(this, rootView);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        return rootView;
    }

    private void logIn() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!CredentialsValidator.username(username)) {
            usernameEditText.setError(getString(R.string.empty_username));
            showSnackBar(R.string.empty_username);
            return;
        }
        if (!CredentialsValidator.password(password)) {
            passwordEditText.setError(getString(R.string.empty_password));
            showSnackBar(R.string.empty_password);
            return;
        }

        User user = Sync.getUser();
        user.setUsername(username);
        user.setPassword(password);
        UserState userState = Sync.getUserState();

        final MaterialDialog dialog = new NonCancelableProgressMaterialDialogBuilder(getActivity())
                .title(R.string.please_wait)
                .content(R.string.logging_in)
                .show();

        userState.login(user).continueWith(new Termination<>(new Termination.Callback<Void>() {
            @Override
            public void cancelled() {
                showSnackBar(R.string.fail);
            }

            @Override
            public void faulted(Exception e) {
                showSnackBar(e.getLocalizedMessage());
            }

            @Override
            public void succeeded(Void result) {
                BUS.post(new UserLoggedInEvent());
            }

            @Override
            public void completed() {
                dialog.dismiss();
            }
        }), Task.UI_THREAD_EXECUTOR);
    }
}
