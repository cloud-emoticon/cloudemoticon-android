package org.ktachibana.cloudemoji.models.memory;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO class holding a name and a list of entries
 */
@org.parceler.Parcel
public class Category {
    String name;
    List<Entry> entries;

    public Category() { /*Required empty bean constructor*/ }

    public Category(String name, List<Entry> entries) {
        this.name = name;
        this.entries = entries;
    }

    public String getName() {
        return name;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof Category))
            return false;

        Category category = (Category) o;

        if (!entries.equals(category.entries))
            return false;
        if (!name.equals(category.name))
            return false;

        return true;
    }
}
