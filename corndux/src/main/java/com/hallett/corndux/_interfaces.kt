package com.hallett.corndux

interface IState
interface Action
object Init: Action
interface SideEffect

sealed interface Actor<State: IState>

interface ActionPerformer<State: IState>: Actor<State> {
    suspend fun performAction(
        state: State,
        action: Action,
        dispatchNewAction: (Action) -> Unit,
    )
}

interface Reducer<State: IState>: Actor<State> {
    fun reduce(
        state: State,
        action: Action,
        dispatchSideEffect: (SideEffect) -> Unit
    ): State
}

interface SideEffectPerformer: Actor<Nothing> {
    fun performSideEffect(sideEffect: SideEffect)
}

interface Middleware<State: IState>: Actor<State> {
    suspend fun before(state: State, action: Action)
    suspend fun afterEachReduce(state: State, action: Action, reducer: Class<out Reducer<State>>)
    suspend fun after(state: State, action: Action)
}
