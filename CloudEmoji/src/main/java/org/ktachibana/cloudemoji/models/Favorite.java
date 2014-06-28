package org.ktachibana.cloudemoji.models;

import android.content.Context;

import com.orm.SugarRecord;

import java.util.List;

/**
 * POJO class holding an entry with arrangingOrder
 */
public class Favorite extends SugarRecord<Favorite> {
    private String emoticon;
    private String description;

    // Query favorite by emoticon SQL clause
    private static final String FIND_BY_EMOTICON_SQL_CLAUSE = "emoticon = ? ";

    public Favorite(Context context) {
        super(context);
    }

    public Favorite(Context context, String emoticon, String description) {
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

    public static List<Favorite> queryByEmoticon(String queriedEmoticon) {
        return Favorite.find(
                Favorite.class,
                FIND_BY_EMOTICON_SQL_CLAUSE,
                new String[]{queriedEmoticon});
    }

}
