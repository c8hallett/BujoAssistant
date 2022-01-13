package com.hallett.taskassistant.ui.formatters

import android.annotation.SuppressLint
import android.util.Log
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_evaluator.IScopeEvaluator
import com.hallett.taskassistant.ui.model.scope.SelectableScope

@SuppressLint("SimpleDateFormat")
class SelectableScopeFormatter(
    private val scopeEvaluator: IScopeEvaluator,
    ): IFormatter<Scope, SelectableScope> {

    private val scopeTypeMap: Map<ScopeType, Pair<Float, (Int) -> String>> = mapOf(
        ScopeType.DAY to Pair(0f, ::formatOffsetLabelDay),
        ScopeType.WEEK to Pair(1.945f, ::formatOffsetLabelWeek),
        ScopeType.MONTH to Pair(3.434f, ::formatOffsetLabelMonth),
        ScopeType.YEAR to Pair(5.900f, ::formatOffsetLabelYear),
    )

    override fun format(input: Scope): SelectableScope = with(input){
        val offset = scopeEvaluator.getOffset(this)
        val (paddingScale, offsetFormatter) = scopeTypeMap.getValue(type)
        SelectableScope(
            ScopeDateFormat.STANDARD.format(this),
            offsetFormatter(offset),
            this,
            paddingScale * 8f
        )
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
