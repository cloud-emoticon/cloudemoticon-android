package org.ktachibana.cloudemoji.activities;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

@org.parceler.Parcel
public class MainActivityState {
    int itemId;
    SourceInMemoryCache sourceCache;

    public MainActivityState() { /*Required empty bean constructor*/ }

    public MainActivityState(SourceInMemoryCache sourceCache) {
        this.itemId = Constants.DEFAULT_LIST_ITEM_ID;
        this.sourceCache = sourceCache;
    }
}
