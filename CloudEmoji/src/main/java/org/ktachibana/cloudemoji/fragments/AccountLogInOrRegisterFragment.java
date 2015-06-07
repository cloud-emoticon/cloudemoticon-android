package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ktachibana.cloudemoji.BaseTabsPagerFragment;
import org.ktachibana.cloudemoji.R;

public class AccountLogInOrRegisterFragment extends BaseTabsPagerFragment {

    public AccountLogInOrRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        mPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        mTabs.setViewPager(mPager);

        return rootView;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.log_in);
            }
            if (position == 1) {
                return getString(R.string.register);
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new AccountLogInFragment();
            }
            if (position == 1) {
                return new AccountRegisterFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
