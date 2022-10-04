package com.hallett.taskassistant.features.createTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.main.corndux.ClearCreateTaskState
import com.hallett.taskassistant.main.corndux.DisplayTaskForEdit
import com.hallett.taskassistant.main.corndux.UpdateSelectedScope
import com.hallett.taskassistant.main.corndux.UpdateTaskName

class CreateTaskReducer : Reducer<CreateTaskState> {
    override fun reduce(state: CreateTaskState, action: Action): CreateTaskState {
        return when (action) {
            is DisplayTaskForEdit -> with(action.task){
                state.copy(
                    taskId = id,
                    taskName = name,
                    taskScope = scope,
                    taskStatus = status
                )
            }
            is UpdateTaskName -> state.copy(taskName = action.taskName)
            is ClearCreateTaskState -> CreateTaskState()
            is UpdateSelectedScope -> state.copy(taskScope = action.scope)
            else -> state
        }
    }

}