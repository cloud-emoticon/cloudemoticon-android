package org.ktachibana.cloudemoji.utils;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.ktachibana.cloudemoji.parsing.SourceJsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BackupUtils {
    public static void readFavorites(InputStream is) throws IOException {
        // Get backed up favorites
        String json = IOUtils.toString(is);
        Source source = new SourceJsonParser().parse(null, json);
        List<Entry> backedUpFavorites = source.getCategories().get(0).getEntries();

        // Get current favorites
        List<Favorite> favorites = Favorite.listAll(Favorite.class);
        List<Entry> currentFavorites = new ArrayList<>();
        for (Favorite favorite : favorites) {
            currentFavorites.add(new Entry(favorite.getEmoticon(), favorite.getDescription()));
        }

        // Merge backed up and current favorites
        Set<Entry> mergedFavorites = new HashSet<>(backedUpFavorites);
        mergedFavorites.addAll(currentFavorites);

        // Remove all current favorites and add back merged favorites
        Favorite.deleteAll(Favorite.class);
        for (Entry entry : mergedFavorites) {
            Favorite favorite = new Favorite(entry.getEmoticon(), entry.getDescription(), "");
            favorite.save();
        }
    }

    public static void writeFavorites(OutputStream os) throws IOException {
        String json = new SourceJsonParser().serialize(FavoritesUtils.getFavoritesAsSource());
        IOUtils.write(json, os);
    }
}
