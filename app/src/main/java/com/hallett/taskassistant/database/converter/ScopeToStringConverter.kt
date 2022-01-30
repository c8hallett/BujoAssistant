package com.hallett.taskassistant.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.hallett.scopes.model.Scope
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScopeToStringConverter {
    private val gson = GsonBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()).create()

    @TypeConverter
    fun scopeToString(scope: Scope?): String? = when(scope) {
        null -> null
        else -> gson.toJson(scope)
    }

    @TypeConverter
    fun stringToScope(value: String?): Scope? = when(value){
        null -> null
        else -> gson.fromJson(value, Scope::class.java)
    }

    private class LocalDateAdapter: JsonSerializer<LocalDate> {
        override fun serialize(
            src: LocalDate?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(src?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        }
    }
}
