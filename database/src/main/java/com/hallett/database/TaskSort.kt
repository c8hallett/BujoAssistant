package com.hallett.database

import com.hallett.database.room.TaskEntity


sealed class TaskSort(val ascending: Boolean, internal val field: String) {
    class Updated(ascending: Boolean): TaskSort(ascending, TaskEntity.UPDATED_AT)
    class Created(ascending: Boolean): TaskSort(ascending, TaskEntity.CREATED_AT)
    class ScopeStart(ascending: Boolean): TaskSort(ascending, TaskEntity.TASK_SCOPE_VALUE)
    class ScopeEnd(ascending: Boolean): TaskSort(ascending, TaskEntity.TASK_SCOPE_END_VALUE)
}