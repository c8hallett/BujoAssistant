package com.hallett.corndux

import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sun.awt.util.PerformanceLogger

abstract class Store<GlobalState: IState, Action: IAction, SideEffect: ISideEffect>(
    initialState: GlobalState,
    performers: List<ActionPerformer<GlobalState, Action, SideEffect>>,
    middlewares: List<Middleware<GlobalState, Action>>,
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
                middlewares.forEach{
                    it.beforeActionPerformed(stateFlow.value, newAction)
                }
                stateFlow.value = performers.fold(stateFlow.value) { state, performer ->
                    performer.performAction(newAction, state, ::dispatch, ::dispatch).also { newState ->
                        middlewares.forEach {
                            it.afterEachPerformer(newState, newAction, performer::class.java)
                        }
                    }
                }
                middlewares.forEach{
                    it.afterActionPerformed(stateFlow.value, newAction)
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

    fun <T> observeState(select: (GlobalState) -> T): StateFlow<T> = stateFlow.map {
        select(it)
    }.stateIn(scope, SharingStarted.WhileSubscribed(), select(stateFlow.value)) // this is so big sad, map{} takes away my ez peasy StateFlow type

}