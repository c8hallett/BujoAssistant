package com.hallett.taskassistant.features.limboTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.main.corndux.UpdateTaskList
import com.hallett.taskassistant.main.corndux.UpdateExpandedTask

class LimboReducer : Reducer<LimboState> {
    override fun reduce(state: LimboState, action: Action): LimboState {
        return when (action) {
            is UpdateTaskList -> state.copy(
                list = action.taskList
            )
            is UpdateExpandedTask -> {
                val newTask = when (state.expandedTask) {
                    action.task -> null
                    else -> action.task
                }
                state.copy(expandedTask = newTask)
            }
            else -> state
        }
    }
}