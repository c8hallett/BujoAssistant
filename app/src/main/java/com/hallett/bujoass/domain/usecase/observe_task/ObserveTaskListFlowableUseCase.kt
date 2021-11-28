package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.presentation.model.Task
import com.hallett.scopes.Scope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber

@ExperimentalCoroutinesApi
class ObserveTaskListFlowableUseCase(
    private val observeTaskListUseCase: IObserveTaskListUseCase,
): IObserveTaskListFlowableUseCase {
    override fun execute(scopes: Flow<Scope?>): Flow<List<Task>> = scopes.flatMapLatest {
        Timber.i("Returning flow for $it")

        observeTaskListUseCase.execute(it)
    }
}