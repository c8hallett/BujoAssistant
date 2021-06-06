package com.hallett.bujoass.database

import androidx.room.TypeConverter
import java.util.Date

class DateToLongConverter {
    @TypeConverter
    fun dateToLong(dateTime: Date?): Long? = dateTime?.time

    @TypeConverter
    fun longToDate(epochMillis: Long?): Date? = when(epochMillis){
        null -> null
        else -> Date(epochMillis)
    }
}