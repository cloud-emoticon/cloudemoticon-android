package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.sync.Sync;
import org.ktachibana.cloudemoji.sync.interfaces.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolts.Continuation;
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

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern mEmailPattern;

    public AccountRegisterFragment() {
        // Required empty public constructor

        mEmailPattern = Pattern.compile(EMAIL_PATTERN);
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
        Matcher emailMatcher = mEmailPattern.matcher(email);

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.empty_username));
            showSnackBar(R.string.empty_username);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.empty_password));
            showSnackBar(R.string.empty_password);
            return;
        }
        if (!emailMatcher.matches()) {
            emailEditText.setError(getString(R.string.illegal_email_address));
            showSnackBar(R.string.illegal_email_address);
            return;
        }

        User newUser = Sync.getUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.register().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                // TODO
                return null;
            }
        });
    }
}
