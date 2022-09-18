package com.hallett.taskassistant.features.futureTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.main.corndux.UpdateExpandedTask

class FutureReducer : Reducer<FutureState> {
    override fun reduce(state: FutureState, action: Action): FutureState {
        return when (action) {
            is UpdateExpandedList -> state.copy(
                list = action.taskList,
                listType = action.listType
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