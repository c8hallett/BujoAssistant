package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.domain.model.TaskStatus

interface IModifyTaskStatusUseCase {
    suspend fun execute(taskId: Long, taskStatus: TaskStatus)
}