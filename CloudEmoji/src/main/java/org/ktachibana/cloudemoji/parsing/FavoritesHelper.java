package org.ktachibana.cloudemoji.parsing;

import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.memory.Category;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.models.memory.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoritesHelper {

    public static Source getFavoritesAsSource() {
        // Build source
        ArrayList<String> information = new ArrayList<String>();
        information.add("favorites");
        List<Category> categories = Arrays.asList(getFavoritesAsCategory());
        return new Source("favorites", information, categories);
    }

    public static Category getFavoritesAsCategory() {
        List<Favorite> favorites = Favorite.listAll(Favorite.class);
        List<Entry> favoriteEntries = new ArrayList<Entry>();
        for (Favorite favorite : favorites) {
            favoriteEntries.add(new Entry(favorite.getEmoticon(), favorite.getDescription()));
        }
        return new Category("favorites", favoriteEntries);
    }

    public static List<Favorite> getFavoritesAsList() {
        return Favorite.listAll(Favorite.class);
    }
}
