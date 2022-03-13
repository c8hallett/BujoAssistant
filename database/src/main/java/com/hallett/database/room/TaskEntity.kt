package com.hallett.database.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.ScopeType
import java.time.LocalDate
import java.util.Date

@Entity(tableName = TaskEntity.TABLE_NAME)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0L,
    @ColumnInfo(name = TASK_NAME)
    val taskName: String,
    @ColumnInfo(name = TASK_STATUS)
    val status: TaskStatus = TaskStatus.INCOMPLETE,
    @Embedded
    val scope: ScopeEntity?,
    @ColumnInfo(name = CREATED_AT)
    val createdAt: Date = Date(),
    @ColumnInfo(name = UPDATED_AT)
    val updatedAt: Date = Date()
) {

    data class ScopeEntity(
        @ColumnInfo(name = TASK_SCOPE_TYPE)
        val type: ScopeType,
        @ColumnInfo(name = TASK_SCOPE_VALUE)
        val value: LocalDate,
    )

    data class StatusUpdate(
        @ColumnInfo(name = ID)
        val taskId: Long,
        @ColumnInfo(name = TASK_STATUS)
        val status: TaskStatus,
        @ColumnInfo(name = UPDATED_AT)
        val updated: Date = Date()
    )

    data class ScopeUpdate(
        @ColumnInfo(name = ID)
        val taskId: Long,
        @Embedded
        val scope: ScopeEntity?,
        @ColumnInfo(name = UPDATED_AT)
        val updatedAt: Date = Date()
    )


    companion object {
        const val TABLE_NAME = "bujo_task"
        const val ID = "id"
        const val TASK_NAME = "task_name"
        const val TASK_SCOPE_TYPE = "task_scope_type"
        const val TASK_SCOPE_VALUE = "task_scope_value"
        const val TASK_STATUS = "task_status"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}