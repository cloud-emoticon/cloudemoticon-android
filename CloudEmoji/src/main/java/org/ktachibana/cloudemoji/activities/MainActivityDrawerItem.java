package org.ktachibana.cloudemoji.activities;

import android.os.Parcel;
import android.os.Parcelable;

public class MainActivityDrawerItem implements Parcelable {
    public static final int ITEM_TYPE_REPOSITORY = 0;
    public static final int ITEM_TYPE_CATEGORY = 1;
    public static final int ITEM_TYPE_OPTIONS = 2;
    public static final int ITEM_TYPE_DIVIDER = 3;

    private int itemType;
    private long id;

    public MainActivityDrawerItem(int itemType, long id) {
        this.itemType = itemType;
        this.id = id;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.itemType);
        dest.writeLong(this.id);
    }

    private MainActivityDrawerItem(Parcel in) {
        this.itemType = in.readInt();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<MainActivityDrawerItem> CREATOR = new Parcelable.Creator<MainActivityDrawerItem>() {
        public MainActivityDrawerItem createFromParcel(Parcel source) {
            return new MainActivityDrawerItem(source);
        }

        public MainActivityDrawerItem[] newArray(int size) {
            return new MainActivityDrawerItem[size];
        }
    };
}
