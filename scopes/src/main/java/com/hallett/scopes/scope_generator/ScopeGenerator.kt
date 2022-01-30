package com.hallett.scopes.scope_generator

import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

internal class ScopeGenerator: IScopeGenerator {

    override fun generateScope(type: ScopeType, date: LocalDate): Scope = when(type) {
        ScopeType.DAY -> Scope(
            type = type,
            value = date,
            chronoUnit = ChronoUnit.DAYS,
        )
        ScopeType.WEEK -> Scope(
            type = type,
            value = date.minusDays(date.dayOfWeek.ordinal.toLong()),
            chronoUnit = ChronoUnit.WEEKS,
        )
        ScopeType.MONTH -> Scope(
            type = type,
            value = date.minusDays(date.dayOfMonth - 1L),
            chronoUnit = ChronoUnit.MONTHS,
        )
        ScopeType.YEAR -> Scope(
            type = type,
            value = date.minusDays(date.dayOfYear - 1L),
            chronoUnit = ChronoUnit.YEARS,
        )
    }

    override fun add(oldScope: Scope, unit: Int): Scope {
        val forwardedDate = oldScope.value.plus(unit.toLong(), oldScope.chronoUnit)
        return with(oldScope) {
            Scope(type, forwardedDate, chronoUnit)
        }
    }
}