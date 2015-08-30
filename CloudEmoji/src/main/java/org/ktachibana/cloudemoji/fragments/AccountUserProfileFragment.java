package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.utils.NonCancelableProgressMaterialDialogBuilder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AccountUserProfileFragment extends BaseFragment {
    @InjectView(R.id.username)
    TextView username;
    @InjectView(R.id.email)
    TextView email;
    @InjectView(R.id.log_out)
    Button logOut;

    public AccountUserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_user_profile, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    private void logOut() {
        final MaterialDialog dialog = new NonCancelableProgressMaterialDialogBuilder(getActivity())
                .title(R.string.please_wait)
                .content(R.string.logging_out)
                .show();
    }
}
