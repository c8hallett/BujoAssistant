package com.hallett.bujoass.database

import androidx.room.TypeConverter
import com.hallett.bujoass.domain.DomainScope

class ScopeToStringTypeConverter {
    @TypeConverter
    fun scopeToString(scope: DomainScope): String = scope.name

    @TypeConverter
    fun stringToScope(name: String): DomainScope = DomainScope.valueOf(name)
}