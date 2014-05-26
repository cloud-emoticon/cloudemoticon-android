package org.ktachibana.cloudemoji.models;

import java.io.Serializable;

/**
 * POJO class holding an emoticon string and its description
 */
public class Entry implements Serializable {
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
