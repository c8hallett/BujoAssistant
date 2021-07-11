package com.hallett.bujoass.database.converter

import androidx.room.TypeConverter
import com.hallett.bujoass.domain.model.DScope

class ScopeToStringTypeConverter {
    @TypeConverter
    fun scopeToString(scope: DScope?): String? = scope?.name

    @TypeConverter
    fun stringToScope(name: String?): DScope? = when(name){
        null -> null
        else -> DScope.valueOf(name)
    }
}