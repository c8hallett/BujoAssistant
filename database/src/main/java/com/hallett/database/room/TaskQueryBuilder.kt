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

internal class TaskQueryBuilder {
    private sealed class ScopeFilter {
        data class ByScopeType(val type: ScopeType?) : ScopeFilter()
        data class ByScope(val scope: Scope, val future: Boolean?) : ScopeFilter()
    }

    private data class StatusFilter(val statuses: List<TaskStatus>, val included: Boolean)

    private lateinit var scopeFilter: ScopeFilter
    private lateinit var taskNameFilter: String
    private lateinit var statusFilter: StatusFilter
    private lateinit var sort: Sort

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
    fun filterByTaskName(nameFilter: String) {
        this.taskNameFilter = nameFilter
    }
    fun filterByStatuses(statuses: List<TaskStatus>, included: Boolean) {
        this.statusFilter = StatusFilter(statuses, included)
    }
    fun setSort(sort: Sort) {
        this.sort = sort
    }

    fun query(operations: TaskQueryBuilder.() -> Unit): SupportSQLiteQuery {
        operations()

        val filters = mutableListOf<String>().apply {
            if(::scopeFilter.isInitialized) {
                when(val s = scopeFilter){
                    is ScopeFilter.ByScopeType -> add("$TASK_SCOPE_TYPE IS ${s.type.toInt()}")
                    is ScopeFilter.ByScope -> {
                        val operation = when(s.future) {
                            true -> ">"
                            false -> "<"
                            else -> "IS"
                        }
                        add("$TASK_SCOPE_TYPE IS ${s.scope.type.toInt()}")
                        add("$TASK_SCOPE_VALUE $operation ${s.scope.value.toLong()}")
                    }
                }
            }

            if(::taskNameFilter.isInitialized) {
                add("$TASK_NAME LIKE %$taskNameFilter%")
            }

            if(::statusFilter.isInitialized) {
                val statusesParam = statusFilter.statuses
                    .map { it.toInt() }
                    .joinToString(
                        separator = ",",
                        prefix = "(",
                        postfix = ")"
                    )
                val operation = if(statusFilter.included) "IN" else "NOT IN"
                add("$TASK_STATUS $operation $statusesParam")
            }
        }

        val order = when {
            ::sort.isInitialized -> "$ORDER_BY ${sort.field} ${if(sort.ascending) "ASC" else "DESC"}"
            else -> ""
        }

        val queryString = SELECT + filters.joinToString(prefix = WHERE, separator = AND) + order
        return SimpleSQLiteQuery(queryString)
    }

    private fun ScopeType?.toInt() = EnumToIntConverter.scopeTypeToInt(this)
    private fun TaskStatus?.toInt() = EnumToIntConverter.taskStatusToInt(this)
    private fun LocalDate?.toLong() = LocalDateToLongConverter.localDateToLong(this)
}