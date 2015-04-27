package org.ktachibana.cloudemoji.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO class holding information and a list of categories
 */
public class Source implements Parcelable {
    private List<String> information;
    private List<Category> categories;

    public Source(ArrayList<String> information, List<Category> categories) {
        this.information = information;
        this.categories = categories;
    }

    public List<String> getInformation() {
        return information;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Entry> getAllEntries() {
        List<Entry> entries = new ArrayList<Entry>();
        for (Category category : categories) {
            entries.addAll(category.getEntries());
        }
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof Source))
            return false;

        Source source = (Source) o;

        if (!categories.equals(source.categories))
            return false;
        if (!information.equals(source.information))
            return false;

        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.information);
        dest.writeTypedList(categories);
    }

    private Source(Parcel in) {
        this.information = new ArrayList<>();
        in.readList(this.information, List.class.getClassLoader());
        this.categories = new ArrayList<>();
        in.readTypedList(categories, Category.CREATOR);
    }

    public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
        public Source createFromParcel(Parcel source) {
            return new Source(source);
        }

        public Source[] newArray(int size) {
            return new Source[size];
        }
    };
}
