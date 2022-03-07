package com.hallett.taskassistant.corndux.reducers

import com.hallett.corndux.Commit
import com.hallett.taskassistant.corndux.IReducer
import com.hallett.taskassistant.corndux.TaskAssistantState

class SessionReducer: IReducer {
    override fun reduce(state: TaskAssistantState, commit: Commit): TaskAssistantState {
        return when(commit) {
            is UpdateCurrentScreen -> state.updateSession { copy(screen = commit.screen) }
            else -> state
        }
    }
}