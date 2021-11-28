package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.presentation.model.Task
import com.hallett.scopes.Scope
import kotlinx.coroutines.flow.Flow

interface IObserveTaskListUseCase {
    fun execute(scope: Scope?): Flow<List<Task>>
}