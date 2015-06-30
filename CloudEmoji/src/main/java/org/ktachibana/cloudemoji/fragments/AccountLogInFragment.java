package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseUser;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.UserLoggedInEvent;
import org.ktachibana.cloudemoji.utils.CredentialsValidator;
import org.ktachibana.cloudemoji.utils.NonCancelableProgressMaterialDialogBuilder;
import org.ktachibana.cloudemoji.utils.Termination;

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

        final MaterialDialog dialog = new NonCancelableProgressMaterialDialogBuilder(getActivity())
                .title(R.string.please_wait)
                .content(R.string.logging_in)
                .show();

        ParseUser.logInInBackground(username, password).continueWith(new Termination<>(new Termination.Callback<ParseUser>() {
            @Override
            public void cancelled() {
                showSnackBar(R.string.fail);
            }

            @Override
            public void faulted(Exception e) {
                showSnackBar(e.getLocalizedMessage());
            }

            @Override
            public void succeeded(ParseUser result) {
                BUS.post(new UserLoggedInEvent());
            }

            @Override
            public void completed() {
                dialog.dismiss();
            }
        }), Task.UI_THREAD_EXECUTOR);
    }
}
