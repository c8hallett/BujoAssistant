package com.hallett.database.converter

import androidx.room.TypeConverter
import com.hallett.scopes.model.ScopeType

class ScopeTypeToIntConverter {
    @TypeConverter
    fun scopeTypeToInt(type: ScopeType?): Int? = type?.ordinal

    @TypeConverter
    fun intToScopeType(value: Int?): ScopeType? = value?.let { ScopeType.values()[it] }
}