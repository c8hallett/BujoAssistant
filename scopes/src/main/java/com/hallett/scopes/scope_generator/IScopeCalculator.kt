package com.hallett.scopes.scope_generator

import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import java.time.LocalDate

interface IScopeCalculator {
    fun generateScope(type: ScopeType = ScopeType.DAY, date: LocalDate = LocalDate.now()): Scope
    fun add(oldScope: Scope, unit: Int = 1): Scope
    fun isCurrentOrFutureScope(scope: Scope): Boolean
    fun isCurrentScope(scope: Scope): Boolean
    fun getOffset(scope: Scope, startDate: LocalDate = LocalDate.now()): Int
    fun getEndOfScope(scope: Scope): LocalDate
}