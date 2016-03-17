package org.ktachibana.cloudemoji.activities;

import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

@org.parceler.Parcel
public class MainActivityState {
    int currentItem;
    SourceInMemoryCache sourceCache;

    public MainActivityState() { /*Required empty bean constructor*/ }

    public MainActivityState(SourceInMemoryCache sourceCache) {
        this.currentItem = 0;
        this.sourceCache = sourceCache;
    }

    public MainActivityState(SourceInMemoryCache sourceCache, int currentItem) {
        this(sourceCache);
        this.currentItem = currentItem;
    }
}
