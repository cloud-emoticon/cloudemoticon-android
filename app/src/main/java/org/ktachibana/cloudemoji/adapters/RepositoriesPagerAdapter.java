package org.ktachibana.cloudemoji.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.ktachibana.cloudemoji.fragments.SourceFragment;
import org.ktachibana.cloudemoji.fragments.SourceFragmentBuilder;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

import java.util.List;

public class RepositoriesPagerAdapter extends FragmentStatePagerAdapter {
    private SourceInMemoryCache mCache;
    private List<Long> mIdList;

    public RepositoriesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public RepositoriesPagerAdapter(FragmentManager fm, SourceInMemoryCache cache) {
        super(fm);
        mCache = cache;
        mIdList = cache.getAllKeys();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mCache.get(fromPositionToKey(position)).getAlias();
    }

    @Override
    public Fragment getItem(int position) {
        return new SourceFragmentBuilder(mCache.get(fromPositionToKey(position))).build();
    }

    @Override
    public int getCount() {
        return mCache.size();
    }

    private long fromPositionToKey(int position) {
        return mIdList.get(position);
    }
}
