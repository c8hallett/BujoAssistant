package com.hallett.bujoass.domain.usecase.modify_task

interface IDeferTaskUseCase {
    suspend fun execute(taskId: Long)
}