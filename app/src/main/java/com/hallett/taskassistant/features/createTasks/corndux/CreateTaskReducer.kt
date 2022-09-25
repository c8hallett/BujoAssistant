package com.hallett.taskassistant.features.createTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.main.corndux.ClearCreateTaskState
import com.hallett.taskassistant.main.corndux.DisplayTaskForEdit
import com.hallett.taskassistant.main.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.main.corndux.UpdateSelectedScope
import com.hallett.taskassistant.main.corndux.UpdateTaskName

class CreateTaskReducer : Reducer<CreateTaskState> {
    override fun reduce(state: CreateTaskState, action: Action): CreateTaskState {
        return when (action) {
            is DisplayTaskForEdit -> state.copy(task = action.task)
            is UpdateTaskName -> {
                val newTask = with(state.task) {
                    copy(name = action.taskName)
                }
                state.copy(task = newTask)
            }
            is ClearCreateTaskState -> CreateTaskState()
            is UpdateScopeSelectionInfo -> state.copy(scopeSelectionInfo = action.scopeSelectionInfo)
            is UpdateSelectedScope -> {
                val newTask = with(state.task) {
                    copy(scope = action.scope)
                }
                state.copy(
                    task = newTask,
                    scopeSelectionInfo = action.scopeSelectionInfo
                )
            }
            else -> state
        }
    }

}