package com.hallett.corndux

import java.util.concurrent.Executors
import java.util.logging.Level
import java.util.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class Store<State : IState>(
    initialState: State,
    actors: List<Actor<out State>>,
    private val scope: CoroutineScope,
) {
    private val stateFlow = MutableStateFlow(initialState)
    private val actionFlow = MutableSharedFlow<Action>()
    private val sideEffectFlow = MutableSharedFlow<SideEffect>()

    private val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    private val performers: List<Performer<in State>> = actors.filterIsInstance<Performer<in State>>()
    private val reducers: List<Reducer<State>> = actors.filterIsInstance<Reducer<State>>()

    init {
        // performing actions as needed
        scope.launch(customDispatcher) {
            actionFlow.collect { newAction ->
                Logger.getLogger("CORNDUX").log(Level.INFO, "${newAction::class.simpleName} ▶ ${this@Store::class.simpleName}")
                val currentState = stateFlow.value
                performers.forEach { actor ->
                    when (actor) {
                        is StatefulPerformer -> actor.performAction(
                            state = currentState,
                            action = newAction,
                            dispatchAction = ::dispatch,
                            dispatchSideEffect = ::dispatchSideEffect,
                        )
                        is StatelessPerformer -> actor.performAction(
                            action = newAction,
                            dispatchAction = ::dispatch,
                            dispatchSideEffect = ::dispatchSideEffect,
                        )
                    }
                }

                val newState = reducers.fold(stateFlow.value) { state, reducer ->
                    reducer.reduce(state, newAction)
                }
                stateFlow.emit(newState)
            }
        }

        dispatch(Init)
    }

    private fun dispatchSideEffect(sideEffect: SideEffect) {
        scope.launch {
            sideEffectFlow.emit(sideEffect)
        }
    }

    fun dispatch(action: Action) {
        scope.launch(customDispatcher) {
            actionFlow.emit(action)
        }
    }

    fun <T> observeState(select: (State) -> T): StateFlow<T> = stateFlow.map {
        select(it)
    }.stateIn(
        scope,
        SharingStarted.WhileSubscribed(),
        select(stateFlow.value)
    ) // this is so big sad, map{} takes away my ez peasy StateFlow type

    fun observeState(): StateFlow<State> = stateFlow

    fun observeSideEffects(): Flow<SideEffect> = sideEffectFlow

    private val prependMap: MutableMap<Int, Job> = mutableMapOf()

    fun prepend (other: Store<out IState>) {
        Logger.getLogger("CORNDUX").log(Level.INFO, "${other::class.simpleName} 👉 ${this::class.simpleName}")
        val prependJob = scope.launch(customDispatcher) {
            launch {
                other.actionFlow.collect { actionFlow.emit(it) }
            }
            launch {
                other.sideEffectFlow.collect { sideEffectFlow.emit(it) }
            }
        }
        prependMap[other.hashCode()] = prependJob
    }

    fun remove (other: Store<out IState>) {
        Logger.getLogger("CORNDUX").log(Level.INFO, "${other::class.simpleName} ❌ ${this::class.simpleName}")
        val prependJob = prependMap.remove(other.hashCode())
        prependJob?.cancel()
    }
}