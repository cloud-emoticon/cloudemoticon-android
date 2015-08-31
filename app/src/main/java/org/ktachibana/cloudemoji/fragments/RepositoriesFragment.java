package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.bundler.ParcelerArgsBundler;

import org.ktachibana.cloudemoji.BaseTabsPagerFragment;
import org.ktachibana.cloudemoji.adapters.RepositoriesPagerAdapter;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

public class RepositoriesFragment extends BaseTabsPagerFragment {
    private static final String ARG_CACHE = "cache";
    @Arg(bundler = ParcelerArgsBundler.class)
    SourceInMemoryCache mCache;

    public RepositoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
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
