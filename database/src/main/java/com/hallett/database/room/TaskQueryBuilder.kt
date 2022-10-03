package com.hallett.database.room

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.hallett.database.Sort
import com.hallett.database.converter.LocalDateToLongConverter
import com.hallett.database.converter.EnumToIntConverter
import com.hallett.database.room.TaskEntity.Companion.TABLE_NAME
import com.hallett.database.room.TaskEntity.Companion.TASK_NAME
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
    }

    private data class StatusFilter(val statuses: List<TaskStatus>, val included: Boolean)

    private var scopeFilter: ScopeFilter? = null
    private var taskNameFilter: String? = null
    private var statusFilter: StatusFilter? = null
    private var sort: Sort? = null

    private companion object{
        const val SELECT = "SELECT * FROM $TABLE_NAME "
        const val WHERE = "WHERE "
        const val AND = " AND "
        const val ORDER_BY = " ORDER BY "
    }

    fun filterByScope(scope: Scope, order: Boolean? = null) {
        this.scopeFilter = ScopeFilter.ByScope(scope, order)
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

    fun setSort(sort: Sort) {
        this.sort = sort
    }
    fun removeSort(){
        this.sort = null
    }

    internal fun query(): SupportSQLiteQuery {

        val filters = mutableListOf<String>().apply {
            scopeFilter?.let { filter ->
                when (filter) {
                    is ScopeFilter.ByScopeType -> {
                        add("$TASK_SCOPE_TYPE IS ${filter.type.toInt()}")
                    }
                    is ScopeFilter.ByScope -> {
                        val operation = when (filter.future) {
                            true -> ">"
                            false -> "<"
                            else -> "IS"
                        }
                        add("$TASK_SCOPE_TYPE IS ${filter.scope.type.toInt()}")
                        add("$TASK_SCOPE_VALUE $operation ${filter.scope.value.toLong()}")
                    }
                }
            }

            taskNameFilter?.let { taskName ->
                if(taskName.isNotBlank()) add("$TASK_NAME LIKE %$taskName%")
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

        val order = sort?.let {
            "$ORDER_BY ${it.field} ${if (it.ascending) "ASC" else "DESC"}"
        } ?: ""

        val queryString = SELECT + filters.joinToString(prefix = WHERE, separator = AND) + order
        return SimpleSQLiteQuery(queryString)
    }

    private fun ScopeType?.toInt() = EnumToIntConverter.scopeTypeToInt(this)
    private fun TaskStatus?.toInt() = EnumToIntConverter.taskStatusToInt(this)
    private fun LocalDate?.toLong() = LocalDateToLongConverter.localDateToLong(this)
}