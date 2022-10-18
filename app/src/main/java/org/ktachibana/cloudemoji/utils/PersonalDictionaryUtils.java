package org.ktachibana.cloudemoji.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.UserDictionary;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.models.disk.Favorite;

import java.util.List;

public class PersonalDictionaryUtils {
    public static int importAllFavorites(ContentResolver contentResolver) {
        List<Favorite> favorites = FavoritesUtils.getFavoritesAsList();

        // Add all favorites into user dictionary
        int counter = 0;
        for (Favorite favorite : favorites) {
            if (!favorite.getShortcut().equals("")) {
                ContentValues newValue = new ContentValues();
                newValue.put(UserDictionary.Words.WORD, favorite.getEmoticon());
                newValue.put(UserDictionary.Words.SHORTCUT, favorite.getShortcut());
                contentResolver.insert(UserDictionary.Words.CONTENT_URI, newValue);
                counter++;
            }
        }

        return counter;
    }

    public static int revokeAllFavorites(ContentResolver contentResolver) {
        List<Favorite> favorites = FavoritesUtils.getFavoritesAsList();

        int counter = 0;
        for (Favorite favorite : favorites) {
            if (!favorite.getShortcut().equals("")) {
                String clause = UserDictionary.Words.WORD + "=?";
                String[] args = {favorite.getEmoticon()};

                contentResolver.delete(UserDictionary.Words.CONTENT_URI, clause, args);
                counter++;
            }
        }

        return counter;
    }
}
