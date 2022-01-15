package com.hallett.taskassistant.ui.formatters

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType

class ScopeScaleFormatter: Formatter<Scope?, Dp> {
    private companion object{
        const val DP_SCALE = 12f
    }
    override fun format(input: Scope?): Dp {
        val factor = when(input?.type) {
            null, ScopeType.DAY -> 0f
            ScopeType.WEEK -> 1.945f
            ScopeType.MONTH -> 3.434f
            ScopeType.YEAR -> 5.900f
        }
        return (factor * DP_SCALE).dp
    }
}