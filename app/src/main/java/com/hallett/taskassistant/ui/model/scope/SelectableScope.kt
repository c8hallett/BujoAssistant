package com.hallett.taskassistant.ui.model.scope

import com.hallett.scopes.model.Scope

data class SelectableScope(
    val label: String,
    val secondaryLabel: String,
    val scope: Scope,
    val extraPadding: Float,
)
