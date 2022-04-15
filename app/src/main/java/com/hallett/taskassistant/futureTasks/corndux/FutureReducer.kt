package com.hallett.taskassistant.futureTasks.corndux

import com.hallett.corndux.Commit
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.corndux.UpdateExpandedTask

class FutureReducer : Reducer<FutureState> {
    override fun reduce(state: FutureState, commit: Commit): FutureState {
        return when (commit) {
            is UpdateExpandedList -> state.copy(
                list = commit.taskList,
                listType = commit.listType
            )
            is UpdateExpandedTask -> {
                val newTask = when (state.expandedTask) {
                    commit.task -> null
                    else -> commit.task
                }
                state.copy(expandedTask = newTask)
            }
            else -> state
        }
    }
}