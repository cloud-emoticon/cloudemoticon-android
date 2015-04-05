package org.ktachibana.cloudemoji.parsing;

import org.ktachibana.cloudemoji.models.Category;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.Favorite;
import org.ktachibana.cloudemoji.models.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoritesHelper {

    public static Source getFavoritesAsSource() {
        // Get all favorite entries
        List<Favorite> favorites = Favorite.listAll(Favorite.class);
        List<Entry> favoriteEntries = new ArrayList<Entry>();
        for (Favorite favorite : favorites) {
            favoriteEntries.add(new Entry(favorite.getEmoticon(), favorite.getDescription()));
        }
        Category favoriteCategory = new Category("favorites", favoriteEntries);

        // Build source
        ArrayList<String> information = new ArrayList<String>();
        information.add("favorites");
        List<Category> categories = Arrays.asList(favoriteCategory);
        return new Source(information, categories);
    }
}
