package com.hallett.bujoass.usecase

import com.hallett.bujoass.domain.DomainScope
import java.util.*

interface ISaveNewTaskUseCase {
    suspend fun execute(task: String, scope: DomainScope?, scopeDate: Date)
}