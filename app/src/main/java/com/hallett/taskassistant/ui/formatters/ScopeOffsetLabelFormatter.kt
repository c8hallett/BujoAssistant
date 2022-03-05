package com.hallett.taskassistant.ui.formatters

import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator

class ScopeOffsetLabelFormatter(private val scopeCalculator: IScopeCalculator) :
    Formatter<Scope?, String> {

    private val scopeTypeMap: Map<ScopeType, (Int) -> String> = mapOf(
        ScopeType.DAY to ::formatOffsetLabelDay,
        ScopeType.WEEK to ::formatOffsetLabelWeek,
        ScopeType.MONTH to ::formatOffsetLabelMonth,
        ScopeType.YEAR to ::formatOffsetLabelYear,
    )

    override fun format(input: Scope?): String = when (input) {
        null -> "Sometime"
        else -> when (val calculateOffsetLabel = scopeTypeMap[input.type]) {
            null -> "Sometime"
            else -> calculateOffsetLabel(scopeCalculator.getOffset(input))
        }
    }

    private fun formatOffsetLabelDay(offset: Int): String = when (offset) {
        0 -> "Today"
        1 -> "Tomorrow"
        else -> "$offset days from now"
    }

    private fun formatOffsetLabelWeek(offset: Int): String = when (offset) {
        0 -> "This week"
        1 -> "Next week"
        else -> "$offset weeks from now"
    }

    private fun formatOffsetLabelMonth(offset: Int): String = when (offset) {
        0 -> "This month"
        1 -> "Next month"
        else -> "$offset months from now"
    }

    private fun formatOffsetLabelYear(offset: Int): String = when (offset) {
        0 -> "This year"
        1 -> "Next year"
        else -> "$offset year from now"
    }
}