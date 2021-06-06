package com.hallett.bujoass.domain.usecase

import com.hallett.bujoass.presentation.model.PScopeInstance

interface ISaveNewTaskUseCase {
    suspend fun execute(taskName: String, scopeInstance: PScopeInstance)
}