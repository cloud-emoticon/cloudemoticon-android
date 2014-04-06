package org.ktachibana.cloudemoji.models;

/**
 * POJO class holding an emoticon string ans description
 */
public class Entry {
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
}
