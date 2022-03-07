package com.hallett.taskassistant.corndux.actors

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.corndux.IState
import com.hallett.corndux.Middleware
import com.hallett.logging.logD

class LoggingMiddleware<State: IState>: Middleware<State> {
    private lateinit var prevState: State
    private lateinit var prevPerformerState: State
    override suspend fun before(state: State, action: Action) {
        prevState = state
        prevPerformerState = state
        logD("<${action::class.java.simpleName} = $action> $state -> ")
    }

    override suspend fun afterEachReduce(
        state: State,
        action: Action,
        reducer: Class<out Reducer<State>>
    ) {
        when(state) {
            prevPerformerState -> logD("(${reducer.simpleName})")
            else -> {
                logD("(${reducer.simpleName}) $state ->")
                prevPerformerState = state
            }
        }
    }

    override suspend fun after(state: State, action: Action) {
        logD("$state </${action::class.java.simpleName}> ")
    }
}