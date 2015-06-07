package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ktachibana.cloudemoji.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountLogInFragment extends Fragment {


    public AccountLogInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_log_in, container, false);
    }


}
