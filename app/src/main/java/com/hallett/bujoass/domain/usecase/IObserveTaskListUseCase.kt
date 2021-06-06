package com.hallett.bujoass.domain.usecase

import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow

interface IObserveTaskListUseCase {
    fun execute(scope: PScopeInstance): Flow<List<Task>>
}