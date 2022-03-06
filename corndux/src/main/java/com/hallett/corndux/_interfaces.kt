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

interface Reducer<State: IState> {
    suspend fun performAction(
        action: Action,
        state: State,
    ): State
}

interface SideEffectPerformer {
    suspend fun performSideEffect(sideEffect: SideEffect)
}

sealed interface Middleware<State: IState>
interface PreMiddleware<State: IState>: Middleware<State> {
    fun beforeActionPerformed(
        state: State,
        action: Action,
        dispatchNewAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    )
}

interface InterMiddleware<State: IState>: Middleware<State> {
    fun afterEachReduce(state: State, action: Action, reducer: Class<out Reducer<State>>)
}

interface PostMiddleware<State: IState>: Middleware<State> {
    fun afterActionPerformed(
        state: State,
        action: Action,
        dispatchNewAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    )
}
