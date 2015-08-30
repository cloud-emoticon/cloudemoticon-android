package org.ktachibana.cloudemoji.models.disk;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * POJO class representing a history record
 */
public class History extends SugarRecord<History> implements Serializable {
    private String emoticon;
    private String description;

    public History() {
    }

    public History(String emoticon, String description) {
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
