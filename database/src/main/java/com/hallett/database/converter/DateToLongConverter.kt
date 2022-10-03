package com.hallett.database.converter

import androidx.room.TypeConverter
import java.util.Date

object DateToLongConverter {
    @TypeConverter
    fun dateToLong(dateTime: Date?): Long? = dateTime?.time

    @TypeConverter
    fun longToDate(epochMillis: Long?): Date? = when (epochMillis) {
        null -> null
        else -> Date(epochMillis)
    }
}