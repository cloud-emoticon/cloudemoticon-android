package org.ktachibana.cloudemoji.database

import android.arch.persistence.room.TypeConverter

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromBoolean(value: Boolean): Int {
        return when (value) {
            true -> 1
            false -> 0
        }
    }

    @TypeConverter
    @JvmStatic
    fun toBoolean(value: Int): Boolean {
        return when (value) {
            1 -> true
            0 -> false
            else -> throw RuntimeException("Cannot convert Int $value to Boolean")
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromFormatType(value: FormatType): Int {
        return when (value) {
            FormatType.XML -> 0
            FormatType.JSON -> 1
        }
    }

    @TypeConverter
    @JvmStatic
    fun toFormatType(value: Int): FormatType {
        return when (value) {
            0 -> FormatType.XML
            1 -> FormatType.JSON
            else -> throw RuntimeException("Cannot convert Int $value to FormatType")
        }
    }
}