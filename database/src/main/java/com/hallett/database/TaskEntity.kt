package com.hallett.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hallett.scopes.model.Scope
import com.hallett.domain.TaskStatus
import java.util.Date

@Entity(tableName = TaskEntity.TABLE_NAME)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0L,
    @ColumnInfo(name = TASK_NAME)
    val taskName: String,
    @ColumnInfo(name = TASK_STATUS)
    val status: com.hallett.domain.TaskStatus = com.hallett.domain.TaskStatus.INCOMPLETE,
    @ColumnInfo(name = TASK_SCOPE)
    val scope: Scope? = null,
    @ColumnInfo(name = CREATED_AT)
    val createdAt: Date = Date(),
    @ColumnInfo(name = UPDATED_AT)
    val updatedAt: Date = Date()
) {

    data class StatusUpdate(
        @ColumnInfo(name = ID)
        val taskId: Long,
        @ColumnInfo(name = TASK_STATUS)
        val status: com.hallett.domain.TaskStatus,
        @ColumnInfo(name = UPDATED_AT)
        val updated: Date = Date()
    )

    data class NewScopeUpdate(
        @ColumnInfo(name = ID)
        val taskId: Long,
        @ColumnInfo(name = TASK_SCOPE)
        val scope: Scope?,
        @ColumnInfo(name = UPDATED_AT)
        val updatedAt: Date = Date()
    )


    companion object {
        const val TABLE_NAME = "bujo_task"
        const val ID = "id"
        const val TASK_NAME = "task_name"
        const val TASK_SCOPE = "task_scope"
        const val TASK_STATUS = "task_status"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}