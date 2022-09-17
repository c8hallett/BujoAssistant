package com.hallett.corndux

import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class Store<State : IState>(
    initialState: State,
    actors: List<Performer<in State>>,
    reducers: List<Reducer<State>>,
    private val scope: CoroutineScope,
) {

    private val stateFlow = MutableStateFlow(initialState)
    private val sideEffectFlow = MutableSharedFlow<SideEffect>()

    private val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
    private val performActionChannel = Channel<Action>()
    private val commitActionChannel = Channel<Action>()

    init {
        // performing actions as needed
        scope.launch(customDispatcher) {
            performActionChannel.consumeEach { newAction ->
                val currentState = stateFlow.value
                val consumed = actors.fold(false) { consumed, actor ->
                    when(consumed) {
                        true -> true
                        false -> when(actor){
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
                }
                if(!consumed) commitActionChannel.send(newAction)
            }
        }

        // allowing actions to modify state
        scope.launch(customDispatcher) {
            commitActionChannel.consumeEach { newCommit ->
                reducers.fold(stateFlow.value) { state, reducer ->
                    reducer.reduce(state, newCommit)
                }
            }
        }
        dispatch(Init)
    }

    private suspend fun dispatchSideEffect(sideEffect: SideEffect) {
        sideEffectFlow.emit(sideEffect)
    }

    fun dispatch(action: Action) {
        scope.launch(customDispatcher) {
            performActionChannel.send(action)
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
}