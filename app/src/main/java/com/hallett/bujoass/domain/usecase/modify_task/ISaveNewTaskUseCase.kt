package com.hallett.bujoass.domain.usecase.modify_task

interface ISaveNewTaskUseCase {
    suspend fun execute(taskName: String, scope: Scope?)
}