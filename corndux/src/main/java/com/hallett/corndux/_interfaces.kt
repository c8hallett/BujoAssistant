package com.hallett.corndux

interface IState
interface SideEffect

interface Action

object Init : Action

sealed interface Actor<State: IState>

sealed interface Performer<State: IState>: Actor<State>

interface StatelessPerformer: Performer<Nothing> {
    fun performAction(
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit,
    )
}

interface StatefulPerformer<State : IState>: Performer<State>  {
    fun performAction(
        state: State,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit,
    )
}

interface Reducer<State : IState>: Actor<State> {
    fun reduce(
        state: State,
        action: Action
    ): State
}