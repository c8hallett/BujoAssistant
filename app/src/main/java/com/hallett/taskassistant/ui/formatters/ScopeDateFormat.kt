package com.hallett.taskassistant.ui.formatters

import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import java.time.format.DateTimeFormatter

enum class ScopeDateFormat(private val defaultText: String, private val formatterMap: Map<ScopeType,String>) {
    STANDARD(
        "Sometime",
        mapOf(
            ScopeType.DAY to "MMM dd, yyyy",
            ScopeType.WEEK to "MMM dd, yyyy",
            ScopeType.MONTH to "MMM yyyy",
            ScopeType.YEAR to "yyyy",
        )
    );

    fun format(scope: Scope?): String = when(scope) {
        null -> "Sometime"
        else -> when(val pattern = formatterMap[scope.type]) {
            null -> "Sometime"
            else -> DateTimeFormatter.ofPattern(pattern).format(scope.value)
        }
    }
}
