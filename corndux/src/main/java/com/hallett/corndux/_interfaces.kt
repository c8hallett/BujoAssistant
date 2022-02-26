package com.hallett.corndux

interface IState
interface IAction
interface ISideEffect

interface ActionPerformer<State: IState, Action: IAction, SideEffect: ISideEffect> {
    suspend fun performAction(
        action: Action,
        state: State,
        dispatchNewAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ): State
}

interface SideEffectPerformer<SideEffect: ISideEffect> {
    suspend fun performSideEffect(sideEffect: SideEffect)
}