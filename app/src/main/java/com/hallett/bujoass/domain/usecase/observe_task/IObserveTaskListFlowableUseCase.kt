package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow

interface IObserveTaskListFlowableUseCase {
    fun execute(scopes: Flow<Scope?>): Flow<List<Task>>
}