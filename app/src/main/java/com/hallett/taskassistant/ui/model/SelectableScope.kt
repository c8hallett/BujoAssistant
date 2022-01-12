package com.hallett.taskassistant.ui.model

import com.hallett.scopes.Scope

data class SelectableScope(
    val label: String,
    val secondaryLabel: String,
    val scope: Scope,
    val extraPadding: Float,
)
