package com.hallett.bujoass.domain.usecase.scope_manipulation

import com.hallett.bujoass.domain.model.DScope
import java.util.*

interface INormalizeDateForScopeUseCase {
    fun execute(scope: DScope, date: Date): Date
}