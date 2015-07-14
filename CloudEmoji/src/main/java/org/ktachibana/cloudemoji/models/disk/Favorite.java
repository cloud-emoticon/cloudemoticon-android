package org.ktachibana.cloudemoji.models.disk;

import android.support.annotation.NonNull;

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
        this(bookmark.getEmoticon(), bookmark.getDescription(), bookmark.getShortcut());
    }

    public static List<Favorite> convert(
            @NonNull List<ParseBookmark> parseBookmarks
    ) {
        List<Favorite> favorites = new ArrayList<>();
        for (ParseBookmark parseBookmark : parseBookmarks) {
            favorites.add(new Favorite(parseBookmark));
        }
        return favorites;
    }

    public static boolean listEquals(
            @NonNull List<Favorite> favorites,
            @NonNull List<ParseBookmark> parseBookmarks
    ) {
        if (favorites.size() != parseBookmarks.size()) return false;
        for (int i = 0; i < favorites.size(); i++) {
            if (!favorites.get(i).getEmoticon().equals(parseBookmarks.get(i).getEmoticon()))
                return false;
        }
        return true;
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
