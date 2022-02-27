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

abstract class Middleware<State: IState, Action: IAction> {
    open fun beforeActionPerformed(state: State, action: Action) {}
    open fun afterEachPerformer(state: State, action: Action, performer: Class<out ActionPerformer<State, Action, out ISideEffect>>) {}
    open fun afterActionPerformed(state: State, action: Action) {}
}