package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ktachibana.cloudemoji.BaseTabsPagerFragment;
import org.ktachibana.cloudemoji.adapters.EmojiconsPagerAdapter;

public class EmojiconsFragment extends BaseTabsPagerFragment {
    public EmojiconsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        // Setup contents
        mPager.setAdapter(new EmojiconsPagerAdapter(getChildFragmentManager()));
        mTabs.setViewPager(mPager);

        return rootView;
    }
}
