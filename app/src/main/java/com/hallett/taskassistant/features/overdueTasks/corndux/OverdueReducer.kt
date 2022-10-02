package com.hallett.taskassistant.features.overdueTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.main.corndux.UpdateTaskList
import com.hallett.taskassistant.main.corndux.UpdateExpandedTask

class OverdueReducer : Reducer<OverdueState> {
    override fun reduce(state: OverdueState, action: Action): OverdueState {
        return when (action) {
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