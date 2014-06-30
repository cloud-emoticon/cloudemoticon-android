package org.ktachibana.cloudemoji.models;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.ktachibana.cloudemoji.Constants;

import java.io.Serializable;
import java.util.List;

/**
 * POJO class holding an entry with arrangingOrder
 */
public class Favorite extends SugarRecord<Favorite> implements Constants, Serializable {
    private String emoticon;
    private String description;

    public Favorite(Context context) {
        super(context);
    }

    public Favorite(Context context, String emoticon, String description) {
        super(context);
        this.emoticon = emoticon;
        this.description = description;
    }

    @SuppressWarnings("unchecked")
    public static List<Favorite> queryByEmoticon(String queriedEmoticon) {
        return Select
                .from(Favorite.class)
                .where(Condition.prop("emoticon").eq(queriedEmoticon))
                .list();
    }

    public String getEmoticon() {
        return emoticon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }
}
