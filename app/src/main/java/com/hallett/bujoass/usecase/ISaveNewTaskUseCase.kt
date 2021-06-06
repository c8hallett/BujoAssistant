package com.hallett.bujoass.usecase

import com.hallett.bujoass.presentation.PresentationScope
import java.util.*

interface ISaveNewTaskUseCase {
    suspend fun execute(task: String, scope: PresentationScope, scopeDate: Date)
}