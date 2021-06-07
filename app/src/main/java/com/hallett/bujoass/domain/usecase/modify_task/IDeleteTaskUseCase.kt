package com.hallett.bujoass.domain.usecase.modify_task

interface IDeleteTaskUseCase {
    suspend fun execute(taskId: Long)
}