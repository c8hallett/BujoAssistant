package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow

interface IObserveCurrentTasksFlowableUseCase {
    fun execute(): Flow<List<Task>>
}