package com.hallett.taskassistant.domain

import com.hallett.scopes.Scope

data class Task(
    val id: Long,
    val taskName: String,
    val scope: Scope?,
    val status: TaskStatus,
)
