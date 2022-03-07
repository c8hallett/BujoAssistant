package com.hallett.corndux

interface IState
interface SideEffect

interface Action
object Init: Action

interface Commit


sealed interface Actor<State: IState>

interface Performer<State: IState>: Actor<State> {
    suspend fun performAction(
        state: State,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchCommit: (Commit) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit,
    )
}

interface Reducer<State: IState>: Actor<State> {
    fun reduce(
        state: State,
        commit: Commit
    ): State
}

interface Middleware<State: IState>: Actor<State> {
    suspend fun before(state: State, commit: Commit)
    suspend fun afterEachReduce(state: State, commit: Commit, reducer: Class<out Reducer<State>>)
    suspend fun after(state: State, commit: Commit)
}
