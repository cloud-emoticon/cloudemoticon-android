package org.ktachibana.cloudemoji.parsing;

import android.os.Environment;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.models.Category;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.Favorite;
import org.ktachibana.cloudemoji.models.Source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BackupAndRestoreHelper implements Constants {

    public boolean backupFavorites() {
        // If external storage not writable
        if (!isExternalStorageWritable()) {
            return false;
        }

        // Get backup file
        File backupFile = new File(FAVORITES_BACKUP_FILE_PATH);
        if (backupFile.exists()) backupFile.delete();

        // Write to file
        try {
            writeFileToExternalStorage(new SourceJsonParser().serialize(getFavoritesAsSource()), backupFile);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean restoreFavorites() {
        // If external storage not readable
        if (!isExternalStorageReadable()) {
            return false;
        }

        // Get backup file
        File backupFile = new File(FAVORITES_BACKUP_FILE_PATH);

        // Read from file
        FileInputStream inputStream = null;
        try {
            // Get backed up favorites
            inputStream = new FileInputStream(backupFile);
            String json = IOUtils.toString(inputStream);
            Source source = new SourceJsonParser().parse(json);
            List<Entry> backedUpFavorites = source.getCategories().get(0).getEntries();

            // Get current favorites
            List<Favorite> favorites = Favorite.listAll(Favorite.class);
            List<Entry> currentFavorites = new ArrayList<Entry>();
            for (Favorite favorite : favorites) {
                currentFavorites.add(new Entry(favorite.getEmoticon(), favorite.getDescription()));
            }

            // Merge backed up and current favorites
            Set<Entry> mergedFavorites = new HashSet<Entry>();
            for (Entry backedUp : backedUpFavorites) {
                mergedFavorites.add(backedUp);
            }
            for (Entry current : currentFavorites) {
                mergedFavorites.add(current);
            }

            // Remove all current favorites and add back merged favorites
            Favorite.deleteAll(Favorite.class);
            for (Entry entry : mergedFavorites) {
                Favorite favorite = new Favorite(entry.getEmoticon(), entry.getDescription());
                favorite.save();
            }
        } catch (IOException e) {
            return false;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return true;
    }

    public void writeFileToExternalStorage(String string, File file) throws IOException {
        // If external storage not writable
        if (!isExternalStorageWritable()) {
            return;
        }

        // Write to file
        FileOutputStream outputStream = new FileOutputStream(file);
        IOUtils.write(string, outputStream);
        IOUtils.closeQuietly(outputStream);
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public Source getFavoritesAsSource() {
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
