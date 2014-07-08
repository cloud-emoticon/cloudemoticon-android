package org.ktachibana.cloudemoji.utils;

import android.os.Parcelable;

import java.util.Arrays;

/**
 * A key/value in-memory cache for parcelable objects
 * Intended to be stored by Android Parcelable mechanism
 * key will be primitive long
 *
 * @param <V> value type is Parcelable objects
 */
public class ParcelableObjectInMemoryCache<V extends Parcelable> {
    private long[] mKeyArray;
    private V[] mValueArray;
    private int mSize;
    public static final int INITIAL_CAPACITY = 4;

    @SuppressWarnings("unchecked")
    public ParcelableObjectInMemoryCache() {
        mKeyArray = new long[INITIAL_CAPACITY];
        mValueArray = (V[]) new Object[INITIAL_CAPACITY];
        mSize = 0;
    }

    /**
     * Put the value in the cache according to key
     * Old value would be replaced if key is duplicated
     *
     * @param key primitive long key
     * @param value new value
     */
    @SuppressWarnings("unchecked")
    public void put(long key, V value) {
        // If cache contains the key, replace value
        Integer i = contains(key);
        if (i != null) {
            mValueArray[i] = value;
            return;
        }

        // Else add to a new slot if enough capacity
        if (mSize < mKeyArray.length) {
            mKeyArray[mSize] = key;
            mValueArray[mSize] = value;
        }

        // Or expand capacity and add to the new slot
        else
        {
            // Copy over with double capacity
            long[] newKeyArray = Arrays.copyOf(mKeyArray, mKeyArray.length * 2);
            V[] newValueArray = Arrays.copyOf(mValueArray, mValueArray.length * 2);

            // Add to the new slot
            newKeyArray[mSize] = key;
            newValueArray[mSize] = value;

            // Reset pointers
            mKeyArray = newKeyArray;
            mValueArray = newValueArray;
        }
        mSize++;
    }

    /**
     * Get the value in the cache according to key
     *
     * @param key primitive long key
     * @return value according to key, or null if not exists
     */
    public V get(long key) {
        Integer i = contains(key);
        return (i == null) ? null : mValueArray[i];
    }

    /**
     * Get number of actual entries in the cache
     *
     * @return number of actual entries in the cache
     */
    public int getSize() {
        return mSize;
    }

    /**
     * Whether key corresponding value exists in the cache
     *
     * @param key primitive long key
     * @return null if value does not exists, position in cache if exists
     */
    public Integer contains(long key) {
        for (int i = 0; i < mKeyArray.length; i++) {
            if (key == mKeyArray[i]) {
                return i;
            }
        }
        return null;
    }
}
