package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.scopes.Scope

interface ISaveNewTaskUseCase {
    suspend fun execute(taskName: String, scope: Scope?)
}