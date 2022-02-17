package com.hallett.taskassistant.ui.formatters

import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import java.time.format.DateTimeFormatter

class ScopeSimpleDateFormatter : Formatter<Scope?, String> {

    private val patternMap = mapOf(
        ScopeType.DAY to "MMM dd, yyyy",
        ScopeType.WEEK to "MMM dd, yyyy",
        ScopeType.MONTH to "MMM yyyy",
        ScopeType.YEAR to "yyyy",
    )
    private val defaultText = "Sometime"

    override fun format(input: Scope?): String = when (input) {
        null -> defaultText
        else -> when (val pattern = patternMap[input.type]) {
            null -> defaultText
            else -> DateTimeFormatter.ofPattern(pattern).format(input.value)
        }
    }
}