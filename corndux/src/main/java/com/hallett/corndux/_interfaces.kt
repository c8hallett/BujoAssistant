package com.hallett.corndux

interface IState
interface SideEffect

interface Action
object Init : Action

interface Commit

interface Event
abstract class Interpreter<State: IState>(private val store: Store<State>) {
    protected abstract fun mapEvent(event: Event): Action?
    fun dispatch(event: Event) {
        mapEvent(event)?.let { action ->
            store.dispatch(action)
        }
    }
}


sealed interface Actor<State : IState>

interface Performer<State : IState> : Actor<State> {
    suspend fun performAction(
        state: State,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit,
    )
}

interface Reducer<State : IState> : Actor<State> {
    fun reduce(
        state: State,
        commit: Commit
    ): State
}

interface Middleware<State : IState> : Actor<State> {
    suspend fun before(state: State, commit: Commit)
    suspend fun afterEachReduce(state: State, commit: Commit, reducer: Class<out Reducer<State>>)
    suspend fun after(state: State, commit: Commit)
}
