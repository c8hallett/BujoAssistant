package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.presentation.model.PScopeInstance

interface ISaveNewTaskUseCase {
    suspend fun execute(taskName: String, scopeInstance: PScopeInstance)
}