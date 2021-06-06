package com.hallett.bujoass.usecase

import com.hallett.bujoass.domain.Scope
import java.util.*

interface ISaveNewTaskUseCase {
    suspend fun execute(task: String, scope: Scope?, scopeDate: Date)
}