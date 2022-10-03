package com.hallett.database.converter

import androidx.room.TypeConverter
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.ScopeType

object EnumToIntConverter {
    @TypeConverter
    fun scopeTypeToInt(type: ScopeType?): Int? = type?.ordinal

    @TypeConverter
    fun intToScopeType(value: Int?): ScopeType? = value?.let { ScopeType.values()[it] }

    @TypeConverter
    fun taskStatusToInt(status: TaskStatus?): Int? = status?.ordinal

    @TypeConverter
    fun intToTaskStatus(value: Int?): TaskStatus? = value?.let { TaskStatus.values()[it] }
}