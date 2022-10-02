package com.hallett.taskassistant.features.taskList.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.main.corndux.UpdateTaskList
import com.hallett.taskassistant.main.corndux.UpdateExpandedTask
import com.hallett.taskassistant.main.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.main.corndux.UpdateSelectedScope

class TaskListReducer : Reducer<TaskListState> {
    override fun reduce(state: TaskListState, action: Action): TaskListState {
        return when (action) {
            is UpdateScopeSelectionInfo -> state.copy(scopeSelectionInfo = action.scopeSelectionInfo)
            is UpdateSelectedScope -> state.copy(
                scope = action.scope,
                scopeSelectionInfo = action.scopeSelectionInfo
            )
            is UpdateTaskList -> state.copy(taskList = action.taskList)
            is UpdateExpandedTask -> {
                val newTask = when (state.currentlyExpandedTask) {
                    action.task -> null
                    else -> action.task
                }
                state.copy(currentlyExpandedTask = newTask)
            }
            else -> state
        }
    }
}