package com.hallett.scopes

import java.util.Date

interface IScopeGenerator {
    fun generateScope(type: ScopeType = ScopeType.DAY, date: Date = Date()): Scope
    
    fun add(oldScope: Scope, unit: Int = 1): Scope
    fun isCurrentScope(scope: Scope): Boolean
    fun getOffset(startDate: Date, scope: Scope): Int
}