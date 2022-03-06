package com.hallett.taskassistant.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.corndux.IState
import com.hallett.corndux.Middleware
import com.hallett.logging.logD

class LoggingMiddleware<State: IState>: Middleware<State> {
    private lateinit var prevState: State
    private lateinit var prevPeformerState: State
    override fun beforeActionPerformed(state: State, action: Action) {
        prevState = state
        prevPeformerState = state
        logD("[$action] Before perform: $state ")
    }

    override fun afterEachPerformer(
        state: State,
        action: Action,
        performer: Class<out Reducer<State>>
    ) {
        if(state != prevPeformerState) logD("[$action] ${performer.simpleName}: $state ")
        prevPeformerState = state
    }

    override fun afterActionPerformed(state: State, action: Action) {
        logD("[$action] Transform $prevState -> $state")
    }
}