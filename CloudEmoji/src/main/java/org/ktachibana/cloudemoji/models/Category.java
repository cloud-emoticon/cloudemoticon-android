package org.ktachibana.cloudemoji.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * POJO class holding a name and a list of entries
 */
public class Category implements Parcelable {
    public static Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    private String name;
    private List<Entry> entries;

    public Category(String name, List<Entry> entries) {
        this.name = name;
        this.entries = entries;
    }

    private Category(Parcel in) {
        this.name = in.readString();
        in.readTypedList(entries, Entry.CREATOR);
    }

    public String getName() {
        return name;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeTypedList(entries);
    }
}
