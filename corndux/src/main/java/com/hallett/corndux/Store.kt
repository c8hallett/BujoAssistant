package com.hallett.corndux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext


@ObsoleteCoroutinesApi
abstract class Store<State: IState, Action: IAction, SideEffect: ISideEffect>(
    initialState: State,
    performers: List<ActionPerformer<State, Action, SideEffect>>,
    sideEffectPerformer: SideEffectPerformer<SideEffect>,
    private val scope: CoroutineScope,
) {
    private val stateFlow = MutableStateFlow(initialState)

    private val actionDispatcher = newSingleThreadContext("action_sender")
    private val actionChannel = Channel<Action>()

    private val effectDispatcher = newSingleThreadContext("effect_sender")
    private val sideEffectChannel = Channel<SideEffect>()

    init {
        scope.launch(newSingleThreadContext("action_receiver")) {
            actionChannel.consumeEach { newAction ->
                stateFlow.value = performers.fold(stateFlow.value) { state, performer ->
                    performer.performAction(newAction, state, ::dispatch, ::dispatch)
                }
            }
        }
        scope.launch(newSingleThreadContext("effect_sender")) {
            sideEffectChannel.consumeEach { newSideEffect ->
                sideEffectPerformer.performSideEffect(newSideEffect)
            }
        }
    }

    fun dispatch(action: Action) {
        scope.launch(actionDispatcher) {
            actionChannel.send(action)
        }
    }

    fun dispatch(sideEffect: SideEffect) {
        scope.launch(effectDispatcher) {
            sideEffectChannel.send(sideEffect)
        }
    }

    fun observeState(): StateFlow<State> = stateFlow
}