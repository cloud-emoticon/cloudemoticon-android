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

public class AccountRegisterFragment extends BaseFragment {
    @InjectView(R.id.username)
    EditText usernameEditText;
    @InjectView(R.id.password)
    EditText passwordEditText;
    @InjectView(R.id.email)
    EditText emailEditText;
    @InjectView(R.id.register)
    Button registerButton;

    public AccountRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_register, container, false);
        ButterKnife.inject(this, rootView);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        return rootView;
    }

    private void register() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();

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
        if (!CredentialsValidator.email(email)) {
            emailEditText.setError(getString(R.string.illegal_email_address));
            showSnackBar(R.string.illegal_email_address);
            return;
        }

        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);

        final MaterialDialog dialog = new NonCancelableProgressMaterialDialogBuilder(getActivity())
                .title(R.string.please_wait)
                .content(R.string.registering)
                .show();

        newUser.signUpInBackground().continueWith(new Termination<>(new Termination.Callback<Void>() {
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
