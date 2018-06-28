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
import org.ktachibana.cloudemoji.events.RepositoriesPagerItemSelectedEvent;
import org.ktachibana.cloudemoji.models.memory.Source;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RepositoriesFragment extends BaseFragment {
    @BindView(R.id.pager)
    protected ViewPager mPager;
    @BindView(R.id.tabs)
    protected PagerSlidingTabStrip mTabs;
    @Arg(bundler = ParcelerArgsBundler.class)
    LinkedHashMap<Long, Source> mCache;
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
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBus.post(new RepositoriesPagerItemSelectedEvent(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }


}
