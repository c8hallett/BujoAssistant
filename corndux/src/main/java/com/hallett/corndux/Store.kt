package com.hallett.corndux

import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ObsoleteCoroutinesApi
abstract class Store<State: IState, Action: IAction, SideEffect: ISideEffect>(
    initialState: State,
    performers: List<ActionPerformer<State, Action, SideEffect>>,
    sideEffectPerformer: SideEffectPerformer<SideEffect>,
    private val scope: CoroutineScope,
) {
    private val stateFlow = MutableStateFlow(initialState)

    private val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
    private val actionChannel = Channel<Action>()
    private val sideEffectChannel = Channel<SideEffect>()

    init {
        scope.launch(customDispatcher) {
            actionChannel.consumeEach { newAction ->
                stateFlow.value = performers.fold(stateFlow.value) { state, performer ->
                    performer.performAction(newAction, state, ::dispatch, ::dispatch)
                }
            }
        }
        scope.launch(customDispatcher) {
            sideEffectChannel.consumeEach { newSideEffect ->
                sideEffectPerformer.performSideEffect(newSideEffect)
            }
        }
    }

    fun dispatch(action: Action) {
        scope.launch(customDispatcher) {
            actionChannel.send(action)
        }
    }

    fun dispatch(sideEffect: SideEffect) {
        scope.launch(customDispatcher) {
            sideEffectChannel.send(sideEffect)
        }
    }

    fun observeState(): StateFlow<State> = stateFlow
}