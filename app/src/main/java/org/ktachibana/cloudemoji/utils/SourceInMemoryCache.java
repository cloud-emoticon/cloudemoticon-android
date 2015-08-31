package org.ktachibana.cloudemoji.utils;

import android.os.Parcel;
import android.os.Parcelable;

import org.ktachibana.cloudemoji.models.memory.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * A key/value in-memory cache for parcelable objects
 * Intended to be stored by Android Parcelable mechanism
 * key will be primitive long
 */
@org.parceler.Parcel
public class SourceInMemoryCache {
    List<Long> mKeyArray;
    List<Source> mValueArray;

    @SuppressWarnings("unchecked")
    public SourceInMemoryCache() {
        mKeyArray = new ArrayList<>();
        mValueArray = new ArrayList<>();
    }

    /**
     * Put the value in the cache according to key
     * Old value would be replaced if key is duplicated
     *
     * @param key   primitive long key
     * @param value new value
     */
    @SuppressWarnings("unchecked")
    public void put(long key, Source value) {
        // If cache contains the key, replace value
        Integer i = contains(key);
        if (i != null) {
            mValueArray.set(i, value);
            return;
        }

        // Else add to a new slot if enough capacity
        mKeyArray.add(key);
        mValueArray.add(value);
    }

    /**
     * Get the value in the cache according to key
     *
     * @param key primitive long key
     * @return value according to key, or null if not exists
     */
    public Source get(long key) {
        Integer i = contains(key);
        return (i == null) ? null : mValueArray.get(i);
    }

    /**
     * Get all valid keys in the cache
     *
     * @return all valid keys in the cache
     */
    public List<Long> getAllKeys() {
        List<Long> keys = new ArrayList<>();
        for (Long key : mKeyArray) {
            if (key != null) {
                keys.add(key);
            }
        }
        return keys;
    }

    /**
     * Get all valid values in the cache
     *
     * @return all valid values in the cache
     */
    @SuppressWarnings("unchecked")
    public List<Source> getAllValues() {
        List<Source> values = new ArrayList<>();
        for (Source value : mValueArray) {
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    /**
     * Get number of actual entries in the cache
     *
     * @return number of actual entries in the cache
     */
    public int size() {
        return mKeyArray.size();
    }

    /**
     * Whether key corresponding value exists in the cache
     *
     * @param key primitive long key
     * @return null if value does not exists, position in cache if exists
     */
    public Integer contains(long key) {
        for (int i = 0; i < mKeyArray.size(); i++) {
            if (key == mKeyArray.get(i)) {
                return i;
            }
        }
        return null;
    }
}
