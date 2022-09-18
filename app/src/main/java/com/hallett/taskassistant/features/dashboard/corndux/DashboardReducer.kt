package com.hallett.taskassistant.features.dashboard.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.mainNavigation.corndux.UpdateExpandedTask
import com.hallett.taskassistant.mainNavigation.corndux.UpdateTypedTaskList

class DashboardReducer : Reducer<DashboardState> {
    override fun reduce(state: DashboardState, action: Action): DashboardState {
        return when (action) {
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