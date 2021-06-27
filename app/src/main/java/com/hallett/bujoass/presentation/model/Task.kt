package com.hallett.bujoass.presentation.model

import com.hallett.bujoass.domain.model.TaskStatus

data class Task(
    val id: Long,
    val taskName: String,
    val scope: PScopeInstance,
    val status: TaskStatus,
    val isCurrentScope: Boolean,
)