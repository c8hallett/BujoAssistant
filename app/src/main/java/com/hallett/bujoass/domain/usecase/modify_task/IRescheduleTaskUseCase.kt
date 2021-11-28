package com.hallett.bujoass.domain.usecase.modify_task

interface IRescheduleTaskUseCase {
    suspend fun execute(taskId: Long, scope: Scope?)
}