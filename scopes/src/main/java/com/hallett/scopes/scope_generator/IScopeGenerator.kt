package com.hallett.scopes.scope_generator

import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import java.time.LocalDate

interface IScopeGenerator {
    fun generateScope(type: ScopeType = ScopeType.DAY, date: LocalDate = LocalDate.now()): Scope
    fun add(oldScope: Scope, unit: Int = 1): Scope
}