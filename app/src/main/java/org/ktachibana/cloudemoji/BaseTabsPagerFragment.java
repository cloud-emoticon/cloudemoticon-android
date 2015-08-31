package org.ktachibana.cloudemoji;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Base fragment with tabs and pager
 */
public class BaseTabsPagerFragment extends BaseFragment {
    @Bind(R.id.pager)
    protected ViewPager mPager;
    @Bind(R.id.tabs)
    protected PagerSlidingTabStrip mTabs;

    public BaseTabsPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_base_tabs_pager, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }
}
