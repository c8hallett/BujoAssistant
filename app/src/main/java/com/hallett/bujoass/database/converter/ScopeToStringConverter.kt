package com.hallett.bujoass.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson

class ScopeToStringConverter {
    @TypeConverter
    fun scopeToString(scope: Scope?): String? = when(scope) {
        null -> null
        else -> Gson().toJson(scope)
    }

    @TypeConverter
    fun stringToScope(value: String?): Scope? = when(value){
        null -> null
        else -> Gson().fromJson(value, Scope::class.java)
    }
}