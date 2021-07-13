package com.hallett.bujoass.presentation.model

import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.domain.model.TaskStatus

data class Task(
    val id: Long,
    val taskName: String,
    val scope: Scope?,
    val status: TaskStatus,
)