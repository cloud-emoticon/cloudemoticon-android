package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ktachibana.cloudemoji.BaseTabsPagerFragment;
import org.ktachibana.cloudemoji.adapters.RepositoriesPagerAdapter;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

public class RepositoriesFragment extends BaseTabsPagerFragment {
    private static final String ARG_CACHE = "cache";
    private SourceInMemoryCache mCache;

    public static RepositoriesFragment newInstance(SourceInMemoryCache cache) {
        RepositoriesFragment fragment = new RepositoriesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CACHE, cache);
        fragment.setArguments(args);
        return fragment;
    }

    public RepositoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCache = getArguments().getParcelable(ARG_CACHE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        // Setup contents
        mPager.setAdapter(new RepositoriesPagerAdapter(getChildFragmentManager(), mCache));
        mTabs.setViewPager(mPager);

        return rootView;
    }


}
