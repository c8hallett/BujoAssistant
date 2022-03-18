package com.hallett.taskassistant.overdueTasks.corndux

import com.hallett.corndux.Commit
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.corndux.UpdateExpandedTask
import com.hallett.taskassistant.corndux.UpdateTaskList

class OverdueReducer : Reducer<OverdueState> {
    override fun reduce(state: OverdueState, commit: Commit): OverdueState {
        return when (commit) {
            is UpdateTaskList -> state.copy(taskList = commit.taskList)
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