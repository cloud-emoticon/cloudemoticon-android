package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.fragments.FavoriteFragment;
import org.ktachibana.cloudemoji.fragments.HistoryFragment;
import org.ktachibana.cloudemoji.fragments.SourceFragmentBuilder;
import org.ktachibana.cloudemoji.models.memory.Source;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositoriesPagerAdapter extends FragmentStatePagerAdapter {
    private LinkedHashMap<Long, Source> mCache;
    private List<Long> mIdList;
    private Context mContext;

    public RepositoriesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public RepositoriesPagerAdapter(Context context, FragmentManager fm, LinkedHashMap<Long, Source> cache) {
        super(fm);
        mContext = context;
        mCache = cache;
        mIdList = new ArrayList<>();
        for (Map.Entry<Long, Source> entry : mCache.entrySet()) {
            mIdList.add(entry.getKey());
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.fav);
        } else if (position == 1) {
            return mContext.getString(R.string.history);
        } else {
            return mCache.get(fromPositionToKey(position - 2)).getAlias();
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FavoriteFragment();
        } else if (position == 1) {
            return new HistoryFragment();
        } else {
            return new SourceFragmentBuilder(mCache.get(fromPositionToKey(position - 2))).build();
        }
    }

    @Override
    public int getCount() {
        return mCache.size() + 2;
    }

    private long fromPositionToKey(int position) {
        return mIdList.get(position);
    }
}
