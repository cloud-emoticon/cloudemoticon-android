package org.ktachibana.cloudemoji.utils;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.models.Category;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.Favorite;
import org.ktachibana.cloudemoji.models.Source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(backupFile);
            IOUtils.write(getBackupString(), outputStream);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        return true;
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

    private String getBackupString() {
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
        Source favoriteSource = new Source(information, categories);

        return new SourceJsonParser().serialize(favoriteSource);
    }
}
