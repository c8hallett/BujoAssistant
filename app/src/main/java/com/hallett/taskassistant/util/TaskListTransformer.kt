package com.hallett.taskassistant.util

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.hallett.domain.model.Task
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.Scope
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.ui.formatters.Formatter
import com.hallett.taskassistant.ui.model.TaskActionType
import com.hallett.taskassistant.ui.model.TaskView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskListTransformer(
    private val scopeCalculator: IScopeCalculator,
    private val scopeLabelFormatter: Formatter<Scope?, String>
) {
    fun transform(
        tasks: Flow<PagingData<Task>>,
        includeHeaders: Boolean
    ): Flow<PagingData<TaskView>> {
        return tasks.map { pagingData ->
            pagingData.map {
                TaskView.TaskHolder(it, getActionsForTask(it))
            }.insertSeparators { task1, task2 ->
                when {
                    !includeHeaders -> null
                    task2 == null -> null
                    task1?.task?.scope != task2.task.scope ->
                        TaskView.HeaderHolder(scopeLabelFormatter.format(task2.task.scope))
                    else -> null
                }
            }
        }
    }


    private fun getActionsForTask(task: Task): List<TaskActionType> =
        mutableListOf<TaskActionType>().apply {
            add(TaskActionType.DELETE)

            when (task.status) {
                TaskStatus.INCOMPLETE -> {
                    task.scope?.let { scope ->
                        if (scopeCalculator.isCurrentOrFutureScope(scope)) {
                            add(TaskActionType.DEFER)
                        }
                    }
                    add(TaskActionType.RESCHEDULE)
                    add(TaskActionType.COMPLETE)
                }
                TaskStatus.COMPLETE -> add(TaskActionType.UNCOMPLETE)
            }
        }
}