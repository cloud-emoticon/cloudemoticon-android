package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.bundler.ParcelerArgsBundler;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.RepositoriesPagerAdapter;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RepositoriesFragment extends BaseFragment {
    @Bind(R.id.pager)
    protected ViewPager mPager;
    @Bind(R.id.tabs)
    protected PagerSlidingTabStrip mTabs;
    @Arg(bundler = ParcelerArgsBundler.class)
    SourceInMemoryCache mCache;
    @Arg
    int currentItem;

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
        View rootView = inflater.inflate(R.layout.fragment_base_tabs_pager, container, false);
        ButterKnife.bind(this, rootView);

        // Setup contents
        mPager.setAdapter(new RepositoriesPagerAdapter(getContext(), getChildFragmentManager(), mCache));
        mTabs.setViewPager(mPager);
        mPager.setCurrentItem(currentItem);

        return rootView;
    }


}
