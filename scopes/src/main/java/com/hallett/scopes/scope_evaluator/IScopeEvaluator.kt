package com.hallett.scopes.scope_evaluator

import com.hallett.scopes.model.Scope
import java.time.LocalDate

interface IScopeEvaluator {
    fun isCurrentOrFutureScope(scope: Scope): Boolean
    fun isCurrentScope(scope: Scope): Boolean
    fun getOffset(scope: Scope, startDate: LocalDate = LocalDate.now()): Int
}