package com.hallett.domain.model

import com.hallett.scopes.model.Scope

data class Task(
    val id: Long,
    val name: String,
    val scope: Scope?,
    val status: TaskStatus,
)
