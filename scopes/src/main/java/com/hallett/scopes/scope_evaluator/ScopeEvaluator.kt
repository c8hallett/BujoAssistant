package com.hallett.scopes.scope_evaluator

import com.hallett.scopes.model.Scope
import java.time.LocalDate

internal class ScopeEvaluator: IScopeEvaluator {
    override fun isCurrentOrFutureScope(scope: Scope): Boolean {
        return !scope.value.isBefore(LocalDate.now())
    }

    override fun isCurrentScope(scope: Scope): Boolean {
        return scope.value.isEqual(LocalDate.now())
    }

    override fun getOffset(scope: Scope, startDate: LocalDate): Int = scope.chronoUnit
        .between(startDate, scope.value)
        .toInt()
}