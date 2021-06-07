package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.presentation.model.PScopeInstance

interface IRescheduleTaskUseCase {
    suspend fun execute(taskId: Long, scope: PScopeInstance)
}