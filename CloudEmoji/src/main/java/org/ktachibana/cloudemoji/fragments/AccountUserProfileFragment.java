package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.sync.Sync;
import org.ktachibana.cloudemoji.sync.interfaces.User;
import org.ktachibana.cloudemoji.sync.interfaces.UserState;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class AccountUserProfileFragment extends Fragment {
    @InjectView(R.id.username)
    TextView username;
    @InjectView(R.id.email)
    TextView email;
    @InjectView(R.id.log_out)
    Button logOut;
    UserState mUserState;

    public AccountUserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_user_profile, container, false);
        ButterKnife.inject(this, rootView);

        mUserState = Sync.getUserState();
        User currentUser = mUserState.getLoggedInUser();
        username.setText(currentUser.getUsername());
        email.setText(currentUser.getEmail());
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        return rootView;
    }

    private void logOut() {
        mUserState.logout().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                // TODO
                return null;
            }
        });
    }
}
