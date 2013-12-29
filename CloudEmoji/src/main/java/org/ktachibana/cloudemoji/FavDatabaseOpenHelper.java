package org.ktachibana.cloudemoji;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_FAV = "favorites";
    public static final String COLUMN_STRING = "string";
    public static final String COLUMN_NOTE = "note";

    private static final String DB_NAME = "mydb.db";
    private static final int DB_VERSION = 1;


    // CREATE SQL string
    private static final String SQL_CREATE
            = "create table "
            + TABLE_FAV + "("
            + COLUMN_STRING + " text not null, "
            + COLUMN_NOTE + " text not null);";

    public FavDatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int prevVersion, int newVersion) {

    }

}
