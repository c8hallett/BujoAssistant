package com.hallett.bujoass.domain.usecase

import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow

interface IObserveTaskListFlowableUseCase {
    fun execute(scopes: Flow<PScopeInstance>): Flow<List<Task>>
}