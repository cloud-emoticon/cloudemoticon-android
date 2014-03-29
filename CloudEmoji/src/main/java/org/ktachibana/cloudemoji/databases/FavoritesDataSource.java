package org.ktachibana.cloudemoji.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.apache.commons.lang.StringEscapeUtils;
import org.ktachibana.cloudemoji.helpers.RepoXmlParser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles interaction between activities and database
 * This class uses Apache Commons Lang from http://commons.apache.org/proper/commons-lang/
 */
public class FavoritesDataSource {

    private SQLiteDatabase db;
    private FavoritesDatabaseOpenHelper openHelper;

    public FavoritesDataSource(Context context) {
        openHelper = new FavoritesDatabaseOpenHelper(context);
    }

    public void open() throws SQLException {
        db = openHelper.getWritableDatabase();
    }

    public void close() {
        openHelper.close();
    }

    public static RepoXmlParser.Entry createEntry(String string, String note) {
        return new RepoXmlParser.Entry(string, note);
    }

    public boolean addEntry(RepoXmlParser.Entry entry) {
        // If entry not found in db
        if (getEntryByString(entry.string) == null) {
            ContentValues value = new ContentValues();
            value.put(FavoritesDatabaseOpenHelper.COLUMN_STRING, entry.string);
            value.put(FavoritesDatabaseOpenHelper.COLUMN_NOTE, entry.note);
            db.insert(FavoritesDatabaseOpenHelper.TABLE_FAV, null, value);
            return true;
        } else {
            return false;
        }
    }

    public RepoXmlParser.Entry getEntryByString(String string) {
        List<RepoXmlParser.Entry> entries = new ArrayList<RepoXmlParser.Entry>();

        // SQL SELECT string
        string = StringEscapeUtils.escapeSql(string);
        String queryString = "SELECT * FROM "
                + FavoritesDatabaseOpenHelper.TABLE_FAV +
                " WHERE " + FavoritesDatabaseOpenHelper.COLUMN_STRING
                + "='" + string + "'";
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                entries.add(createEntry(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        if (entries.isEmpty()) {
            return null;
        } else {
            return entries.get(0);
        }
    }

    public List<RepoXmlParser.Entry> getAllEntries() {
        List<RepoXmlParser.Entry> entries = new ArrayList<RepoXmlParser.Entry>();

        // SQL SELECT string
        String queryString = "SELECT * FROM " + FavoritesDatabaseOpenHelper.TABLE_FAV;
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                entries.add(createEntry(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        return entries;
    }

    public void updateEntryByString(String string, RepoXmlParser.Entry newEntry) {
        ContentValues value = new ContentValues();
        value.put(FavoritesDatabaseOpenHelper.COLUMN_STRING, newEntry.string);
        value.put(FavoritesDatabaseOpenHelper.COLUMN_NOTE, newEntry.note);
        db.update(FavoritesDatabaseOpenHelper.TABLE_FAV,
                value,
                FavoritesDatabaseOpenHelper.COLUMN_STRING + " = ?",
                new String[]{string});
    }

    public void removeEntryByString(String string) {
        // SQL DELETE string
        string = StringEscapeUtils.escapeSql(string);
        String queryString = "DELETE FROM "
                + FavoritesDatabaseOpenHelper.TABLE_FAV
                + " WHERE " + FavoritesDatabaseOpenHelper.COLUMN_STRING
                + "='" + string + "'";
        db.execSQL(queryString);
    }

}
