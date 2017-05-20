package org.ktachibana.cloudemoji.models.disk;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * POJO class holding an entry with arrangingOrder
 */
public class Favorite extends SugarRecord implements Serializable, Reorderable<Favorite> {
    private String emoticon;
    private String description;
    private String shortcut = "";
    private long lastModifiedTime = System.currentTimeMillis();

    public Favorite() {
    }

    public Favorite copy() {
        Favorite clonedFavorite = new Favorite(this.emoticon, this.description, this.shortcut);
        clonedFavorite.lastModifiedTime = this.lastModifiedTime;
        return clonedFavorite;
    }

    public void overwrite(Favorite favorite) {
        this.emoticon = favorite.getEmoticon();
        this.description = favorite.getDescription();
        this.shortcut = favorite.getShortcut();
        this.lastModifiedTime = favorite.getLastModifiedTime();
        this.save();
    }

    public Favorite(String emoticon, String description, String shortcut) {
        this.emoticon = emoticon;
        this.description = description;
        this.shortcut = shortcut;
        this.lastModifiedTime = System.currentTimeMillis();
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
        updateLastModifiedTime();
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
        updateLastModifiedTime();
    }

    private void updateLastModifiedTime() {
        this.lastModifiedTime = System.currentTimeMillis();
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o == null) return false;
//        if (!(o instanceof Favorite)) return false;
//        Favorite another = (Favorite) o;
//        if (!this.emoticon.equals(another.emoticon)) return false;
//        if (!this.description.equals(another.description)) return false;
//        if (!this.shortcut.equals(another.shortcut)) return false;
//        return true;
//    }
}
