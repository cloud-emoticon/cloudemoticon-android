package org.ktachibana.cloudemoji.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;

import com.rockerhieu.emojicon.emoji.Emojicon;
import com.rockerhieu.emojicon.emoji.Nature;
import com.rockerhieu.emojicon.emoji.Objects;
import com.rockerhieu.emojicon.emoji.People;
import com.rockerhieu.emojicon.emoji.Places;
import com.rockerhieu.emojicon.emoji.Symbols;

import org.ktachibana.cloudemoji.fragments.EmojiconsGridFragment;
import org.ktachibana.cloudemoji.fragments.EmojiconsGridFragmentBuilder;
import org.ktachibana.cloudemoji.utils.EmojiconOfferable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class EmojiconsPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayMap<String, EmojiconOfferable> mData;
    private List<String> mKeys;

    public EmojiconsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

        // Fill emojicons data
        mData = new ArrayMap<>();
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

        return new EmojiconsGridFragmentBuilder(data).build();
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
