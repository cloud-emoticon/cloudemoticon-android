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
 * This module uses Apache Commons Lang from http://commons.apache.org/proper/commons-lang/
 */
public class FavDataSource {

    private SQLiteDatabase db;
    private FavDatabaseOpenHelper openHelper;

    public FavDataSource(Context context) {
        openHelper = new FavDatabaseOpenHelper(context);
    }

    public void open() throws SQLException {
        db = openHelper.getWritableDatabase();
    }

    public void close() {
        openHelper.close();
    }

    public RepoXmlParser.Entry createEntry(String string, String note) {
        return new RepoXmlParser.Entry(string, note);
    }

    public boolean addEntry(RepoXmlParser.Entry entry) {
        // If entry not found in db
        if (getEntryByString(entry.string) == null) {
            ContentValues value = new ContentValues();
            value.put(FavDatabaseOpenHelper.COLUMN_STRING, entry.string);
            value.put(FavDatabaseOpenHelper.COLUMN_NOTE, entry.note);
            db.insert(FavDatabaseOpenHelper.TABLE_FAV, null, value);
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
                + FavDatabaseOpenHelper.TABLE_FAV +
                " WHERE " + FavDatabaseOpenHelper.COLUMN_STRING
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
        String queryString = "SELECT * FROM " + FavDatabaseOpenHelper.TABLE_FAV;
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                entries.add(createEntry(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        return entries;
    }

    public void removeEntryByString(String string) {
        // SQL DELETE string
        string = StringEscapeUtils.escapeSql(string);
        String queryString = "DELETE FROM "
                + FavDatabaseOpenHelper.TABLE_FAV
                + " WHERE " + FavDatabaseOpenHelper.COLUMN_STRING
                + "='" + string + "'";
        db.execSQL(queryString);
    }

}
