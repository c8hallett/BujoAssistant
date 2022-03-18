package com.hallett.taskassistant.dashboard.corndux

import com.hallett.corndux.Commit
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.corndux.UpdateExpandedTask
import com.hallett.taskassistant.corndux.UpdateTypedTaskList

class DashboardReducer : Reducer<DashboardState> {
    override fun reduce(state: DashboardState, commit: Commit): DashboardState {
        return when (commit) {
            is UpdateTypedTaskList -> state.copy(
                scopeType = commit.scopeType,
                taskList = commit.taskList
            )
            is UpdateExpandedTask -> {
                val newTask = when (state.currentlyExpandedTask) {
                    commit.task -> null
                    else -> commit.task
                }
                state.copy(currentlyExpandedTask = newTask)
            }
            else -> state
        }
    }
}