package org.ktachibana.cloudemoji.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "REPOSITORY")
data class Repository(
        @PrimaryKey
        @ColumnInfo(name = "URL")
        val url: String,
        @ColumnInfo(name = "ALIAS")
        val alias: String,
        @ColumnInfo(name = "FORMAT_TYPE")
        val formatType: FormatType,
        @ColumnInfo(name = "FILE_NAME")
        val fileName: String,
        @ColumnInfo(name = "IS_AVAILABLE")
        val isAvailable: Boolean,
        @ColumnInfo(name = "IS_VISIBLE")
        val isVisible: Boolean
)