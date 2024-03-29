package com.hallett.database.room

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.hallett.database.TaskSort
import com.hallett.database.converter.EnumToIntConverter
import com.hallett.database.converter.LocalDateToLongConverter
import com.hallett.database.room.TaskEntity.Companion.TABLE_NAME
import com.hallett.database.room.TaskEntity.Companion.TASK_NAME
import com.hallett.database.room.TaskEntity.Companion.TASK_SCOPE_END_VALUE
import com.hallett.database.room.TaskEntity.Companion.TASK_SCOPE_TYPE
import com.hallett.database.room.TaskEntity.Companion.TASK_SCOPE_VALUE
import com.hallett.database.room.TaskEntity.Companion.TASK_STATUS
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import java.time.LocalDate

class TaskQueryBuilder {
    private sealed class ScopeFilter {
        data class ByScopeType(val type: ScopeType?) : ScopeFilter()
        data class ByScope(val scope: Scope, val future: Boolean?) : ScopeFilter()
        data class ByDate(val date: LocalDate, val future: Boolean?) : ScopeFilter()
    }

    private data class StatusFilter(val statuses: List<TaskStatus>, val included: Boolean)

    private var scopeFilter: ScopeFilter? = null
    private var taskNameFilter: String? = null
    private var statusFilter: StatusFilter? = null
    private var taskSort: TaskSort? = null

    private companion object {
        const val SELECT = "SELECT * FROM $TABLE_NAME "
        const val WHERE = "WHERE "
        const val AND = " AND "
        const val ORDER_BY = " ORDER BY "
    }

    fun filterByDate(localDate: LocalDate, future: Boolean? = null) {
        this.scopeFilter = ScopeFilter.ByDate(localDate, future)
    }

    fun filterByScope(scope: Scope?, future: Boolean? = null) {
        this.scopeFilter = when (scope) {
            null -> ScopeFilter.ByScopeType(null)
            else -> ScopeFilter.ByScope(scope, future)
        }
    }

    fun filterByScopeType(scopeType: ScopeType?) {
        this.scopeFilter = ScopeFilter.ByScopeType(scopeType)
    }

    fun removeScopeFilter() {
        this.scopeFilter = null
    }

    fun filterByTaskName(nameFilter: String) {
        this.taskNameFilter = nameFilter
    }

    fun removeTaskNameFilter() {
        this.taskNameFilter = null
    }

    fun filterByStatuses(statuses: List<TaskStatus>, included: Boolean) {
        this.statusFilter = StatusFilter(statuses, included)
    }

    fun removeStatusFilter() {
        this.statusFilter = null
    }

    fun sortBy(taskSort: TaskSort) {
        this.taskSort = taskSort
    }

    fun removeSort() {
        this.taskSort = null
    }

    internal fun query(): SupportSQLiteQuery {

        val filters = mutableListOf<String>().apply {
            scopeFilter?.let { filter ->
                when (filter) {
                    is ScopeFilter.ByScopeType -> {
                        add("$TASK_SCOPE_TYPE IS ${filter.type.toInt()}")
                    }
                    is ScopeFilter.ByScope -> {
                        add("$TASK_SCOPE_TYPE IS ${filter.scope.type.toInt()}")
                        when (filter.future) {
                            true -> add("$TASK_SCOPE_VALUE > ${filter.scope.value.toLong()}")
                            false -> add("$TASK_SCOPE_END_VALUE < ${filter.scope.value.toLong()}")
                            else -> add("$TASK_SCOPE_VALUE IS ${filter.scope.value.toLong()}")
                        }
                    }
                    is ScopeFilter.ByDate -> {
                        when (filter.future) {
                            true -> add("$TASK_SCOPE_VALUE > ${filter.date.toLong()}")
                            false -> add("$TASK_SCOPE_END_VALUE < ${filter.date.toLong()}")
                            else -> add("$TASK_SCOPE_VALUE IS ${filter.date.toLong()}")
                        }
                    }
                }
            }

            taskNameFilter?.let { taskName ->
                if (taskName.isNotBlank()) add("$TASK_NAME LIKE '%$taskName%'")
            }

            statusFilter?.let { status ->
                val statusesParam = status.statuses
                    .map { it.toInt() }
                    .joinToString(
                        separator = ",",
                        prefix = "(",
                        postfix = ")"
                    )
                val operation = if (status.included) "IN" else "NOT IN"
                add("$TASK_STATUS $operation $statusesParam")
            }
        }

        val order = taskSort?.let { sort ->
            val sortOrder = "$ORDER_BY ${sort.field} ${if (sort.ascending) "ASC" else "DESC"}"
            when (sort) {
                is TaskSort.ScopeEnd, is TaskSort.ScopeStart -> "$sortOrder, $TASK_SCOPE_TYPE DESC"
                else -> sortOrder
            }
        } ?: ""

        val queryString = SELECT + filters.joinToString(prefix = WHERE, separator = AND) + order
        return SimpleSQLiteQuery(queryString)
    }

    private fun ScopeType?.toInt() = EnumToIntConverter.scopeTypeToInt(this)
    private fun TaskStatus?.toInt() = EnumToIntConverter.taskStatusToInt(this)
    private fun LocalDate?.toLong() = LocalDateToLongConverter.localDateToLong(this)
}