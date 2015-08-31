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

import butterknife.Bind;
import butterknife.ButterKnife;

public class AccountUserProfileFragment extends BaseFragment {
    @Bind(R.id.username)
    TextView username;
    @Bind(R.id.email)
    TextView email;
    @Bind(R.id.log_out)
    Button logOut;

    public AccountUserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_user_profile, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void logOut() {
        final MaterialDialog dialog = new NonCancelableProgressMaterialDialogBuilder(getActivity())
                .title(R.string.please_wait)
                .content(R.string.logging_out)
                .show();
    }
}
