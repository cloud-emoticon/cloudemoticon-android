package org.ktachibana.cloudemoji.models;

import android.content.Context;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * POJO class representing a history record
 */
public class History extends SugarRecord<History> implements Serializable {
    private String emoticon;
    private String description;

    public History(Context context) {
        super(context);
    }

    public History(Context context, String emoticon, String description) {
        super(context);
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
