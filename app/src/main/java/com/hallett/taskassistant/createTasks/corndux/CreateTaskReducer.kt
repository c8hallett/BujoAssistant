package com.hallett.taskassistant.createTasks.corndux

import com.hallett.corndux.Commit
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.corndux.ClearCreateTaskState
import com.hallett.taskassistant.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.corndux.UpdateSelectedScope

class CreateTaskReducer : Reducer<CreateTaskState> {
    override fun reduce(state: CreateTaskState, commit: Commit): CreateTaskState {
        return when (commit) {
            is ClearCreateTaskState -> CreateTaskState()
            is UpdateScopeSelectionInfo -> state.copy(scopeSelectionInfo = commit.scopeSelectionInfo)
            is UpdateSelectedScope -> state.copy(scope = commit.scope, scopeSelectionInfo = commit.scopeSelectionInfo)
            else -> state
        }
    }
}