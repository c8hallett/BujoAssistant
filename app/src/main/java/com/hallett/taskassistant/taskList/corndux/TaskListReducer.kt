package com.hallett.taskassistant.taskList.corndux

import com.hallett.corndux.Commit
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.createTasks.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.createTasks.corndux.UpdateSelectedScope
import com.hallett.taskassistant.dashboard.corndux.UpdateExpandedTask
import com.hallett.taskassistant.overdueTasks.corndux.UpdateTaskList

class TaskListReducer: Reducer<TaskListState> {
    override fun reduce(state: TaskListState, commit: Commit): TaskListState {
        return when(commit) {
            is UpdateScopeSelectionInfo -> state.copy(scopeSelectionInfo = commit.scopeSelectionInfo)
            is UpdateSelectedScope -> state.copy(
                scope = commit.scope,
                scopeSelectionInfo = commit.scopeSelectionInfo
            )
            is UpdateTaskList -> state.copy(taskList = commit.taskList)
            is UpdateExpandedTask -> {
                val newTask = when(state.currentlyExpandedTask) {
                    commit.task -> null
                    else -> commit.task
                }
                state.copy( currentlyExpandedTask = newTask)
            }
            else -> state
        }
    }
}