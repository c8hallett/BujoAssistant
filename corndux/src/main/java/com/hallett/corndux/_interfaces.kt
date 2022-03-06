package com.hallett.corndux

interface IState
interface Action
interface SideEffect

interface Event
abstract class Interpreter(private val store: Store<out IState>) {
    abstract fun mapEvent(event: Event): Action?
    fun dispatchEvent(event: Event) = mapEvent(event)?.let { action ->
        store.dispatch(action)
    }
}

interface ActionPerformer<State: IState> {
    suspend fun performAction(
        action: Action,
        state: State,
        dispatchNewAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ): State
}

interface SideEffectPerformer {
    suspend fun performSideEffect(sideEffect: SideEffect)
}

interface Middleware<State: IState> {
    fun beforeActionPerformed(state: State, action: Action) {}
    fun afterEachPerformer(state: State, action: Action, performer: Class<out ActionPerformer<State>>) {}
    fun afterActionPerformed(state: State, action: Action) {}
}