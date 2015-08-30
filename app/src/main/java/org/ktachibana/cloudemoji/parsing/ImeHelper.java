package org.ktachibana.cloudemoji.parsing;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.UserDictionary;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.utils.SystemUtils;

import java.util.List;

public class ImeHelper implements Constants {
    public static int importAllFavoritesIntoIme(ContentResolver contentResolver) {
        List<Favorite> favorites = FavoritesHelper.getFavoritesAsList();

        // Add all favorites into user dictionary
        int counter = 0;
        for (Favorite favorite : favorites) {
            if (!favorite.getShortcut().equals("")) {
                ContentValues newValue = new ContentValues();
                newValue.put(UserDictionary.Words.APP_ID, USER_DICTIONARY_APP_ID);
                newValue.put(UserDictionary.Words.WORD, favorite.getEmoticon());
                if (SystemUtils.aboveJellybean()) {
                    newValue.put(UserDictionary.Words.SHORTCUT, favorite.getShortcut());
                }
                contentResolver.insert(UserDictionary.Words.CONTENT_URI, newValue);
                counter++;
            }
        }

        return counter;
    }

    public static int revokeAllFavoritesFromIme(ContentResolver contentResolver) {
        String clause = UserDictionary.Words.APP_ID + "=?";
        String[] args = {USER_DICTIONARY_APP_ID};

        // TODO: Remove all entries belonging to this app
        return contentResolver.delete(UserDictionary.Words.CONTENT_URI, clause, args);
    }
}
