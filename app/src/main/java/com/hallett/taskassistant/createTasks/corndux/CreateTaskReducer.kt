package com.hallett.taskassistant.createTasks.corndux

import com.hallett.corndux.Commit
import com.hallett.corndux.Reducer

class CreateTaskReducer: Reducer<CreateTaskState> {
    override fun reduce(state: CreateTaskState, commit: Commit): CreateTaskState {
        return when(commit) {
            is ClearCreateTaskState -> CreateTaskState()
            is UpdateSelectedScopeInfo -> state.copy(scopeSelectionInfo = commit.selectionInfo)
            is UpdateSelectedScope -> state.copy(scope = commit.scope)
            else -> state
        }
    }
}