package com.hallett.taskassistant.ui.formatters

import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class ScopeSimpleLabelFormatter(private val scopeCalculator: IScopeCalculator) :
    Formatter<Scope?, String> {
    private companion object {
        const val DAY_PATTERN = "MMM dd, yyyy"
        const val WEEK_PATTERN = "MMM dd, yyyy"
        const val MONTH_PATTERN = "MMMM"
        const val MONTH_YEAR_PATTERN = "MMMM yyyy"
        const val YEAR_PATTERN = "yyyy"
    }

    private val defaultText = "Sometime"

    override fun format(input: Scope?): String = when (input?.type) {
        null -> defaultText
        ScopeType.DAY -> formatDay(scopeCalculator.getOffset(input), input.value)
        ScopeType.WEEK -> formatWeek(scopeCalculator.getOffset(input), input.value)
        ScopeType.MONTH -> formatMonth(scopeCalculator.getOffset(input), input.value)
        ScopeType.YEAR -> formatYear(scopeCalculator.getOffset(input), input.value)
    }

    private fun formatDay(offset: Int, date: LocalDate) = when {
        offset < -1 -> "${abs(offset)} days ago"
        offset == -1 -> "yesterday"
        offset == 0 -> "today"
        offset == 1 -> "tomorrow"
        offset < 7 -> "$offset days from now"
        else -> "on ${DAY_PATTERN.formatDate(date)}"
    }

    private fun formatWeek(offset: Int, date: LocalDate) = when {
        offset < -1 -> "${abs(offset)} weeks ago"
        offset == -1 -> "last week"
        offset == 0 -> "this week"
        offset == 1 -> "next week"
        offset < 4 -> "$offset weeks from now"
        else -> "during the week of ${WEEK_PATTERN.formatDate(date)}"
    }

    private fun formatMonth(offset: Int, date: LocalDate) = when {
        offset < -1 -> "${abs(offset)} months ago"
        offset == -1 -> "last month"
        offset == 0 -> "this month"
        offset == 1 -> "next month"
        date.year == LocalDate.now().year -> "in ${MONTH_PATTERN.formatDate(date)}"
        else -> "in ${MONTH_YEAR_PATTERN.formatDate(date)}"
    }

    private fun formatYear(offset: Int, date: LocalDate) = when {
        offset < -1 -> "${abs(offset)} years ago"
        offset == -1 -> "last year"
        offset == 0 -> "this year"
        offset == 1 -> "next year"
        else -> "in ${YEAR_PATTERN.formatDate(date)}"
    }

    private fun String.formatDate(date: LocalDate): String =
        DateTimeFormatter.ofPattern(this).format(date)
}