package com.hallett.taskassistant.ui.formatters

import android.annotation.SuppressLint
import android.util.Log
import com.hallett.scopes.IScopeGenerator
import com.hallett.scopes.Scope
import com.hallett.scopes.ScopeType
import com.hallett.taskassistant.R
import com.hallett.taskassistant.ui.model.SelectableScope
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

@SuppressLint("SimpleDateFormat")
class SelectableScopeFormatter(private val scopeGenerator: IScopeGenerator): IFormatter<Scope, SelectableScope> {

    private val dayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val weekFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val yearFormatter = DateTimeFormatter.ofPattern("yyyy")

    override fun format(input: Scope): SelectableScope = with(input){
        when(type) {
            ScopeType.DAY -> SelectableScope(
                dayFormatter.format(value),
                formatOffsetLabelDay(scopeGenerator.getOffset(this)),
                this,
                0f
            )
            ScopeType.WEEK -> SelectableScope(
                weekFormatter.format(value),
                formatOffsetLabelWeek(scopeGenerator.getOffset(this)),
                this,
                1.945f * 4
            )
            ScopeType.MONTH -> SelectableScope(
                monthFormatter.format(value),
                formatOffsetLabelMonth(scopeGenerator.getOffset(this)),
                this,
                3.434f * 4
            )
            ScopeType.YEAR -> SelectableScope(
                yearFormatter.format(value),
                formatOffsetLabelYear(scopeGenerator.getOffset(this)),
                this,
                5.900f * 4
            )
        }
    }.also { Log.i("SelectableScopeFormatter", "New selectable: $it") }

    private fun formatOffsetLabelDay(offset: Int): String = when(offset) {
        0 -> "Today"
        1 -> "Tomorrow"
        else -> "$offset days from now"
    }

    private fun formatOffsetLabelWeek(offset: Int): String = when(offset) {
        0 -> "This week"
        1 -> "Next week"
        else -> "$offset weeks from now"
    }

    private fun formatOffsetLabelMonth(offset: Int): String = when(offset) {
        0 -> "This month"
        1 -> "Next month"
        else -> "$offset months from now"
    }

    private fun formatOffsetLabelYear(offset: Int): String = when(offset) {
        0 -> "This year"
        1 -> "Next year"
        else -> "$offset year from now"
    }
}
