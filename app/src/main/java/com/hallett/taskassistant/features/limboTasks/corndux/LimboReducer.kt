package com.hallett.taskassistant.features.limboTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.features.limboTasks.SearchUpdated
import com.hallett.taskassistant.main.corndux.UpdateExpandedTask
import com.hallett.taskassistant.main.corndux.UpdateTaskList
import com.hallett.taskassistant.ui.genericTaskList.CancelRescheduleTask
import com.hallett.taskassistant.ui.genericTaskList.ClickRescheduleTask
import com.hallett.taskassistant.ui.genericTaskList.SubmitRescheduleTask

class LimboReducer : Reducer<LimboState> {
    override fun reduce(state: LimboState, action: Action): LimboState {
        return when (action) {
            is ClickRescheduleTask -> state.copy(
                currentlySchedulingTask = action.task
            )
            is CancelRescheduleTask -> state.copy(
                currentlySchedulingTask = null
            )
            is SubmitRescheduleTask -> state.copy(
                currentlySchedulingTask = null
            )
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
            is SearchUpdated -> state.copy(search = action.newSearch)
            else -> state
        }
    }
}