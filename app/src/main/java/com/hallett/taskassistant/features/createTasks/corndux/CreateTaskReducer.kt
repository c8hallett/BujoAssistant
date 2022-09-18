package com.hallett.taskassistant.features.createTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.mainNavigation.corndux.ClearCreateTaskState
import com.hallett.taskassistant.mainNavigation.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.mainNavigation.corndux.UpdateSelectedScope

class CreateTaskReducer : Reducer<CreateTaskState> {
    override fun reduce(state: CreateTaskState, action: Action): CreateTaskState {
        return when (action) {
            is ClearCreateTaskState -> CreateTaskState()
            is UpdateScopeSelectionInfo -> state.copy(scopeSelectionInfo = action.scopeSelectionInfo)
            is UpdateSelectedScope -> state.copy(
                scope = action.scope,
                scopeSelectionInfo = action.scopeSelectionInfo
            )
            else -> state
        }
    }
}