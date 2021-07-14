package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow

interface IObserveTaskListUseCase {
    fun execute(scope: Scope?): Flow<List<Task>>
}