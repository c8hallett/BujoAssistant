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

abstract class Store<State: IState>(
    initialState: State,
    actors: List<Actor<out State>>,
    private val scope: CoroutineScope,
) {

    private val stateFlow = MutableStateFlow(initialState)
    private val sideEffectFlow = MutableSharedFlow<SideEffect>()

    private val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    private val actionChannel = Channel<Action>()
    private val commitChannel = Channel<Commit>()

    init {
        val performer = actors.filterIsInstance<Performer<State>>()
        val reducers = actors.filterIsInstance<Reducer<State>>()
        val middleware = actors.filterIsInstance<Middleware<State>>()

        // consuming commits
        scope.launch(customDispatcher) {
            commitChannel.consumeEach { newCommit ->

                middleware.forEach {
                    it.before(stateFlow.value, newCommit)
                }

                // pass actions through each reducer
                stateFlow.value = reducers.fold(stateFlow.value) { state, reducer ->
                    reducer.reduce(state, newCommit).also { newState ->
                        middleware.forEach {
                            it.afterEachReduce(newState, newCommit, reducer::class.java)
                        }
                    }
                }

                middleware.forEach {
                    it.after(stateFlow.value, newCommit)
                }
            }
        }

        // consuming actions
        scope.launch(customDispatcher) {
            actionChannel.consumeEach { newAction ->
                // pass actions to pre-middlewares first
                performer.forEach{
                    it.performAction(stateFlow.value, newAction, ::dispatch, ::dispatch, ::dispatch)
                }
            }
        }

        dispatch(Init)
    }

    fun dispatch(action: Action) {
        scope.launch(customDispatcher) {
            actionChannel.send(action)
        }
    }

    private fun dispatch(commit: Commit) {
        scope.launch(customDispatcher) {
            commitChannel.send(commit)
        }
    }

    private fun dispatch(sideEffect: SideEffect) {
        scope.launch(customDispatcher) {
            sideEffectFlow.emit(sideEffect)
        }
    }

    fun <T> observeState(select: (State) -> T): StateFlow<T> = stateFlow.map {
        select(it)
    }.stateIn(scope, SharingStarted.WhileSubscribed(), select(stateFlow.value)) // this is so big sad, map{} takes away my ez peasy StateFlow type


    fun observeSideEffects(): Flow<SideEffect> = sideEffectFlow
}