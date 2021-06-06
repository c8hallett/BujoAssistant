package com.hallett.bujoass.usecase

import com.hallett.bujoass.domain.DomainScope
import java.util.*

interface INormalizeDateForScopeUseCase {
    fun execute(scope: DomainScope, date: Date): Date
}