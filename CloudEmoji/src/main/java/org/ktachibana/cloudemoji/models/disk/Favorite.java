package org.ktachibana.cloudemoji.models.disk;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.models.remote.ParseBookmark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO class holding an entry with arrangingOrder
 */
public class Favorite extends SugarRecord<Favorite> implements Constants, Serializable {
    private String emoticon;
    private String description;
    private String shortcut = "";

    public Favorite() {
    }

    public Favorite(String emoticon, String description, String shortcut) {
        this.emoticon = emoticon;
        this.description = description;
        this.shortcut = shortcut;
    }

    public Favorite(ParseBookmark bookmark) {
        this.emoticon = bookmark.getEmoticon();
        this.description = bookmark.getDescription();
        this.shortcut = bookmark.getShortcut();
    }

    public static List<Favorite> convert(List<ParseBookmark> bookmarks) {
        List<Favorite> favorites = new ArrayList<>();
        for (ParseBookmark bookmark : bookmarks) {
            favorites.add(new Favorite(bookmark));
        }
        return favorites;
    }

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

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }
}
