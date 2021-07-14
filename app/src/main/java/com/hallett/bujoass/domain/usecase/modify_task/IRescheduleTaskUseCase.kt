package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.domain.Scope

interface IRescheduleTaskUseCase {
    suspend fun execute(taskId: Long, scope: Scope?)
}