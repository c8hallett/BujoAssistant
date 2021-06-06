package com.hallett.bujoass.domain.usecase

import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat

@ExperimentalCoroutinesApi
class ObserveTaskListFlowableUseCase(
    private val observeTaskListUseCase: IObserveTaskListUseCase,
): IObserveTaskListFlowableUseCase {
    override fun execute(scopes: Flow<PScopeInstance>): Flow<List<Task>> = scopes.flatMapConcat {
        observeTaskListUseCase.execute(it)
    }
}