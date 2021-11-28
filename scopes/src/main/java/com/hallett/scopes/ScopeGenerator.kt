package com.hallett.scopes

import java.time.temporal.ChronoUnit
import java.util.*

internal class ScopeGenerator: IScopeGenerator {
    override fun generateScope(type: ScopeType, date: Date): Scope = when(type) {
        ScopeType.DAY -> Scope(
            type = type,
            value = truncate(date){},
            chronoUnit = ChronoUnit.DAYS,
            calendarField = Calendar.DAY_OF_YEAR
        )
        ScopeType.WEEK -> Scope(
            type = type,
            value = truncate(date){
                // remove extra days from start of week
                  add(Calendar.DAY_OF_WEEK, 1 - get(Calendar.DAY_OF_WEEK))
            },
            chronoUnit = ChronoUnit.WEEKS,
            calendarField = Calendar.WEEK_OF_YEAR
        )
        ScopeType.MONTH -> Scope(
            type = type,
            value = truncate(date){
                // remove extra days from start of month
                add(Calendar.DAY_OF_MONTH, 1 - get(Calendar.DAY_OF_MONTH))
            },
            chronoUnit = ChronoUnit.MONTHS,
            calendarField = Calendar.MONTH
        )
        ScopeType.YEAR -> Scope(
            type = type,
            value = truncate(date){
                // remove extra days from start of year
                add(Calendar.DAY_OF_YEAR, 1 - get(Calendar.DAY_OF_YEAR))
            },
            chronoUnit = ChronoUnit.YEARS,
            calendarField = Calendar.YEAR
        )
    }

    override fun add(oldScope: Scope, unit: Int): Scope {
        val forwardedDate = Calendar.getInstance().apply {
            time = oldScope.value
            add(oldScope.calendarField, unit)
        }.time

        return with(oldScope) {
            Scope(type, forwardedDate, chronoUnit, calendarField)
        }
    }

    override fun isCurrentScope(scope: Scope): Boolean {
        return scope == generateScope(scope.type)
    }

    override fun getOffset(startDate: Date, scope: Scope): Int = with(scope) {
        chronoUnit.between(
            value.toInstant(),
            startDate.toInstant()
        ).toInt()
    }

    private fun truncate(date: Date, truncateOtherFields: Calendar.() -> Unit): Date = Calendar.getInstance().apply {
        time = date
        // remove all values smaller than the day (essentially setting to midnight of each day)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        truncateOtherFields()
    }.time

}