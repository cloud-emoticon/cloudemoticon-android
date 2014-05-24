package org.ktachibana.cloudemoji.models;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * POJO class holding an entry with order
 */
public class Favorite extends SugarRecord<Favorite> {
    private String emoticon;
    private String description;
    private int order;

    public Favorite(Context context) {
        super(context);
    }

    public Favorite(Context context, String emoticon, String description, int order) {
        super(context);
        this.emoticon = emoticon;
        this.description = description;
        this.order = order;
    }

    public String getEmoticon() {
        return emoticon;
    }

    public String getDescription() {
        return description;
    }

    public int getOrder() {
        return order;
    }
}
