package com.hallett.taskassistant.domain

import com.hallett.scopes.model.Scope

data class Task(
    val id: Long,
    val taskName: String,
    val scope: Scope?,
    val status: TaskStatus,
)
