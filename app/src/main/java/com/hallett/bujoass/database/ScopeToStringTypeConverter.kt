package com.hallett.bujoass.database

import androidx.room.TypeConverter
import com.hallett.bujoass.domain.model.DScope

class ScopeToStringTypeConverter {
    @TypeConverter
    fun scopeToString(scope: DScope): String = scope.name

    @TypeConverter
    fun stringToScope(name: String): DScope = DScope.valueOf(name)
}