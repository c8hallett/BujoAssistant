package com.hallett.bujoass.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hallett.bujoass.domain.DomainScope
import java.util.*

@Entity(tableName = BujoTaskEntity.TABLE_NAME)
data class BujoTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = TASK_NAME)
    val taskName: String,
    @Embedded
    val scopeInfo: ScopeInfo? = null,
    @ColumnInfo(name = CREATED_AT)
    val createdAt: Date = Date(),
    @ColumnInfo(name = UPDATED_AT)
    val updatedAt: Date = Date()
) {

    data class ScopeInfo(
        @ColumnInfo(name = TASK_SCOPE)
        val taskScope: DomainScope = DomainScope.DAY,
        @ColumnInfo(name = SCOPE_VALUE)
        val scopeValue: Date = Date(),
    )

    companion object{
        const val TABLE_NAME = "bujo_task"
        const val TASK_NAME = "task_name"
        const val TASK_SCOPE = "task_scope"
        const val SCOPE_VALUE = "scope_value"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}