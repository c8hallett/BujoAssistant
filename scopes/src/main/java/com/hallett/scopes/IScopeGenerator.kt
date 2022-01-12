package com.hallett.scopes

import java.time.LocalDate

interface IScopeGenerator {
    fun generateScope(type: ScopeType = ScopeType.DAY, date: LocalDate = LocalDate.now()): Scope
    
    fun add(oldScope: Scope, unit: Int = 1): Scope
    fun isCurrentOrFutureScope(scope: Scope): Boolean
    fun isCurrentScope(scope: Scope): Boolean
    fun getOffset(scope: Scope, startDate: LocalDate = LocalDate.now()): Int
}