package org.ktachibana.cloudemoji.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

class MigrationFrom2To3: Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table FAVORITE add SHORTCUT TEXT;")
    }
}

class MigrationFrom3To4: Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table FAVORITE add LAST_MODIFIED_TIME INTEGER;")
    }
}
