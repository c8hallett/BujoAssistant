package com.hallett.corndux

interface IState
interface SideEffect

interface Action

object Init : Action

sealed interface Performer<State: IState>

interface StatelessPerformer: Performer<IState> {
    fun performAction(
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit,
    ): Boolean
}

interface StatefulPerformer<State : IState>: Performer<State>  {
    suspend fun performAction(
        state: State,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit,
    ): Boolean
}

interface Reducer<State : IState> {
    fun reduce(
        state: State,
        action: Action
    ): State
}