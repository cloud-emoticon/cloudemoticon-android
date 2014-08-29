package org.ktachibana.cloudemoji.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.ktachibana.cloudemoji.fragments.EmojiconsGridFragment;
import org.ktachibana.cloudemoji.utils.emojicon.Emojicon;
import org.ktachibana.cloudemoji.utils.emojicon.EmojiconOfferable;
import org.ktachibana.cloudemoji.utils.emojicon.Nature;
import org.ktachibana.cloudemoji.utils.emojicon.Objects;
import org.ktachibana.cloudemoji.utils.emojicon.People;
import org.ktachibana.cloudemoji.utils.emojicon.Places;
import org.ktachibana.cloudemoji.utils.emojicon.Symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EmojiconsPagerAdapter extends FragmentStatePagerAdapter {
    private HashMap<String, EmojiconOfferable> mData;
    private List<String> mKeys;

    public EmojiconsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

        // Fill emojicons data
        mData = new LinkedHashMap<String, EmojiconOfferable>();
        mData.put("NATURE", new Nature());
        mData.put("OBJECTS", new Objects());
        mData.put("PEOPLE", new People());
        mData.put("PLACES", new Places());
        mData.put("SYMBOLS", new Symbols());

        // Fill key set
        mKeys = new ArrayList<String>(mData.keySet());
    }

    @Override
    public Fragment getItem(int i) {
        // Get emojicon data
        Emojicon[] data = mData.get(mKeys.get(i)).offer();

        return EmojiconsGridFragment.newInstance(data);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mKeys.get(position);
    }
}
