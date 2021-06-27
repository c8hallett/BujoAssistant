package com.hallett.bujoass.domain.usecase.scope_operations

import com.hallett.bujoass.domain.model.DScope
import java.util.*

interface IScopeOperatorFactory {
    fun create(scope: DScope, rawDate: Date): IScopeOperator
}