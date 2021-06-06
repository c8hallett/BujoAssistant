package com.hallett.bujoass.database

import androidx.room.TypeConverter
import com.hallett.bujoass.domain.Scope

class ScopeToStringTypeConverter {
    @TypeConverter
    fun scopeToString(scope: Scope): String = scope.name

    @TypeConverter
    fun stringToScope(name: String): Scope = Scope.valueOf(name)
}