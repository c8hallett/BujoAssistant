package com.hallett.database

import com.hallett.database.room.TaskEntity


sealed class Sort(val ascending: Boolean, internal val field: String) {
    class Updated(ascending: Boolean): Sort(ascending, TaskEntity.UPDATED_AT)
    class Created(ascending: Boolean): Sort(ascending, TaskEntity.CREATED_AT)
}