package com.hallett.taskassistant.corndux

import com.hallett.corndux.ActionPerformer
import com.hallett.corndux.ISideEffect
import com.hallett.logging.logD

class LoggingMiddleware: IMiddleware() {
    private lateinit var prevState: TaskAssistantState

    override fun beforeActionPerformed(state: TaskAssistantState, action: TaskAssistantAction) {
        prevState = state
        logD("[$action] Before perform: $state ")
    }

    override fun afterEachPerformer(
        state: TaskAssistantState,
        action: TaskAssistantAction,
        performer: Class<out ActionPerformer<TaskAssistantState, TaskAssistantAction, out ISideEffect>>
    ) {
        logD("[$action] After $performer: $state ")
    }

    override fun afterActionPerformed(state: TaskAssistantState, action: TaskAssistantAction) {
        super.afterActionPerformed(state, action)
        logD("[$action] Transform $prevState -> $state")
    }
}