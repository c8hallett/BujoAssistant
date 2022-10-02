package com.hallett.database.converter

import androidx.room.TypeConverter
import java.time.LocalDate

object LocalDateToLongConverter {
    @TypeConverter
    fun localDateToLong(localDate: LocalDate?): Long? = localDate?.let { it.toEpochDay() }

    @TypeConverter
    fun longToLocalDate(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(value) }
}
