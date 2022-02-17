package com.hallett.taskassistant.database.converter

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.hallett.scopes.model.Scope
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScopeToStringConverter {
    private val gson =
        GsonBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()).create()

    @TypeConverter
    fun scopeToString(scope: Scope?): String? = when (scope) {
        null -> null
        else -> gson.toJson(scope)
    }

    @TypeConverter
    fun stringToScope(value: String?): Scope? = when (value) {
        null -> null
        else -> gson.fromJson(value, Scope::class.java)
    }

    private class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        override fun serialize(
            src: LocalDate?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement = JsonPrimitive(src?.format(DateTimeFormatter.ISO_LOCAL_DATE))

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDate = LocalDate.parse(
            json?.asString,
            DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.ENGLISH)
        )
    }
}
