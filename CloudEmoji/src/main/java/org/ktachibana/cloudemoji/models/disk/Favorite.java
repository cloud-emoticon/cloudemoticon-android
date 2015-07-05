package org.ktachibana.cloudemoji.models.disk;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.models.remote.ParseBookmark;

import java.io.Serializable;
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
        this(bookmark.getEmoticon(), bookmark.getDescription(), bookmark.getShortcut());
    }

    private static List<Favorite> queryListByEmoticon(String queriedEmoticon) {
        return Select
                .from(Favorite.class)
                .where(Condition.prop("emoticon").eq(queriedEmoticon))
                .list();
    }

    public static Favorite queryByEmoticon(String queriedEmoticon) {
        List<Favorite> favorites = queryListByEmoticon(queriedEmoticon);
        if (favorites == null || favorites.size() == 0) {
            return null;
        }
        return favorites.get(0);
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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Favorite)) return false;
        Favorite another = (Favorite) o;
        if (!this.emoticon.equals(another.emoticon)) return false;
        if (!this.description.equals(another.description)) return false;
        if (!this.shortcut.equals(another.shortcut)) return false;
        return true;
    }
}
