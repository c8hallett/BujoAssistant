package com.hallett.scopes.scope_generator

import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal class ScopeCalculator : IScopeCalculator {

    private val truncateMap: Map<ScopeType, (LocalDate) -> LocalDate> = mapOf(
        ScopeType.DAY to { date -> date },
        ScopeType.WEEK to { date -> date.minusDays(date.dayOfWeek.ordinal.toLong()) },
        ScopeType.MONTH to { date -> date.minusDays(date.dayOfMonth - 1L) },
        ScopeType.YEAR to { date -> date.minusDays(date.dayOfYear - 1L) }
    )

    private val chronoUnitMap: Map<ScopeType, ChronoUnit> = mapOf(
        ScopeType.DAY to ChronoUnit.DAYS,
        ScopeType.WEEK to ChronoUnit.WEEKS,
        ScopeType.MONTH to ChronoUnit.MONTHS,
        ScopeType.YEAR to ChronoUnit.YEARS
    )

    private fun ScopeType.truncate(date: LocalDate) = truncateMap.getValue(this).invoke(date)
    private fun ScopeType.toChronoUnit() = chronoUnitMap.getValue(this)

    override fun generateScope(type: ScopeType, date: LocalDate): Scope = Scope(
        type = type,
        value = type.truncate(date),
        chronoUnit = type.toChronoUnit()
    )

    override fun isCurrentOrFutureScope(scope: Scope): Boolean {
        val currentScopeDate = scope.type.truncate(LocalDate.now())
        return !scope.value.isBefore(currentScopeDate)
    }

    override fun isCurrentScope(scope: Scope): Boolean {
        val currentScopeDate = scope.type.truncate(LocalDate.now())
        return scope.value.isEqual(currentScopeDate)
    }

    override fun add(oldScope: Scope, unit: Int): Scope {
        val forwardedDate = oldScope.value.plus(unit.toLong(), oldScope.chronoUnit)
        return with(oldScope) {
            Scope(type, forwardedDate, chronoUnit)
        }
    }

    override fun getOffset(scope: Scope, startDate: LocalDate): Int = scope.chronoUnit
        .between(scope.type.truncate(startDate), scope.value)
        .toInt()
}