package org.ktachibana.cloudemoji.models.memory;

/**
 * POJO class holding an emoticon string and its description
 */
@org.parceler.Parcel
public class Entry {
    String emoticon;
    String description;

    public Entry() { /*Required empty bean constructor*/ }

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
}
