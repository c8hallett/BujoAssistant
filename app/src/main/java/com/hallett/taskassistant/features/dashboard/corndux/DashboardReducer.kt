package com.hallett.taskassistant.features.dashboard.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.main.corndux.UpdateExpandedTask
import com.hallett.taskassistant.main.corndux.UpdateTypedTaskList
import com.hallett.taskassistant.ui.genericTaskList.CancelRescheduleTask
import com.hallett.taskassistant.ui.genericTaskList.ClickRescheduleTask
import com.hallett.taskassistant.ui.genericTaskList.SubmitRescheduleTask

class DashboardReducer : Reducer<DashboardState> {
    override fun reduce(state: DashboardState, action: Action): DashboardState {
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
            is UpdateTypedTaskList -> state.copy(
                scopeType = action.scopeType,
                taskList = action.taskList
            )
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