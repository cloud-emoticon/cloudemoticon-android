package org.ktachibana.cloudemoji.parsing;

import android.os.Environment;

import com.orm.SugarApp;

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
        try {
            writeFileToExternalStorage(getBackupString(), backupFile);
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
            inputStream = new FileInputStream(backupFile);
            String json = IOUtils.toString(inputStream);
            Source source = new SourceJsonParser().parse(json);
            List<Entry> entries = source.getCategories().get(0).getEntries();

            for (Entry entry : entries) {
                String emoticon = entry.getEmoticon();
                String description = entry.getDescription();
                Favorite favorite = new Favorite(SugarApp.getSugarContext(), emoticon, description);
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
