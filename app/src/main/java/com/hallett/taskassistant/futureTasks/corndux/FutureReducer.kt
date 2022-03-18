package com.hallett.taskassistant.futureTasks.corndux

import com.hallett.corndux.Commit
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.dashboard.corndux.UpdateExpandedTask

class FutureReducer: Reducer<FutureState> {
    override fun reduce(state: FutureState, commit: Commit): FutureState {
        return when(commit) {
            is UpdateExpandedList -> state.copy(
                currentList = commit.taskList,
                currentListType = commit.listType
            )
            is UpdateExpandedTask -> {
                val newTask = when(state.currentlyExpandedTask) {
                    commit.task -> null
                    else -> commit.task
                }
                state.copy(currentlyExpandedTask = newTask)
            }
            else -> state
        }
    }
}