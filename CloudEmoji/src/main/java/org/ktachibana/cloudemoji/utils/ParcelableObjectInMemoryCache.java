package org.ktachibana.cloudemoji.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A key/value in-memory cache for parcelable objects
 * Intended to be stored by Android Parcelable mechanism
 * key will be primitive long
 *
 * @param <V> value type is Parcelable objects
 */
public class ParcelableObjectInMemoryCache<V extends Parcelable> implements Parcelable {
    public static final int INITIAL_CAPACITY = 4;
    public static final Parcelable.Creator<ParcelableObjectInMemoryCache> CREATOR = new Parcelable.Creator<ParcelableObjectInMemoryCache>() {
        public ParcelableObjectInMemoryCache createFromParcel(Parcel source) {
            return new ParcelableObjectInMemoryCache(source);
        }

        public ParcelableObjectInMemoryCache[] newArray(int size) {
            return new ParcelableObjectInMemoryCache[size];
        }
    };
    private long[] mKeyArray;
    private V[] mValueArray;
    private int mSize;

    @SuppressWarnings("unchecked")
    public ParcelableObjectInMemoryCache() {
        mKeyArray = new long[INITIAL_CAPACITY];
        mValueArray = (V[]) new Parcelable[INITIAL_CAPACITY];
        mSize = 0;
    }

    @SuppressWarnings("unchecked")
    private ParcelableObjectInMemoryCache(Parcel in) {
        this.mKeyArray = in.createLongArray();
        this.mValueArray = (V[]) in.readParcelableArray(Parcelable[].class.getClassLoader());
        this.mSize = in.readInt();
    }

    /**
     * Put the value in the cache according to key
     * Old value would be replaced if key is duplicated
     *
     * @param key   primitive long key
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
        else {
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
     * Get all valid values in the cache
     * @return all valid values in the cache
     */
    @SuppressWarnings("unchecked")
    public List<V> getAllValues() {
        List<V> values = new ArrayList<V>();
        for (V value : mValueArray) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLongArray(this.mKeyArray);
        dest.writeParcelableArray(this.mValueArray, flags);
        dest.writeInt(this.mSize);
    }
}
