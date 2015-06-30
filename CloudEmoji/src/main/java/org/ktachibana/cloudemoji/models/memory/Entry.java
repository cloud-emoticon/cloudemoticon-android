package org.ktachibana.cloudemoji.models.memory;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO class holding an emoticon string and its description
 */
public class Entry implements Parcelable {
    private String emoticon;
    private String description;

    public Entry(String emoticon, String description) {
        this.emoticon = emoticon;
        this.description = description;
    }

    public String getEmoticon() {
        return emoticon;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || !(o instanceof Entry))
            return false;

        Entry entry = (Entry) o;

        return emoticon.equals(entry.emoticon) && description.equals(entry.description);
    }

    @Override
    public int hashCode() {
        return emoticon.hashCode() + description.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.emoticon);
        dest.writeString(this.description);
    }

    private Entry(Parcel in) {
        this.emoticon = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
        public Entry createFromParcel(Parcel source) {
            return new Entry(source);
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
