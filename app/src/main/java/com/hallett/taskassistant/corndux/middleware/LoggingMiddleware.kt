package com.hallett.taskassistant.corndux.middleware

import com.hallett.corndux.Commit
import com.hallett.corndux.Reducer
import com.hallett.corndux.IState
import com.hallett.corndux.Middleware
import com.hallett.logging.logD

class LoggingMiddleware<State: IState>: Middleware<State> {
    private lateinit var prevState: State
    private lateinit var prevPerformerState: State
    override suspend fun before(state: State, commit: Commit) {
        prevState = state
        prevPerformerState = state
        logD("<${commit::class.java.simpleName} = $commit> $state -> ")
    }

    override suspend fun afterEachReduce(
        state: State,
        commit: Commit,
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

    override suspend fun after(state: State, commit: Commit) {
        logD("$state </${commit::class.java.simpleName}> ")
    }
}