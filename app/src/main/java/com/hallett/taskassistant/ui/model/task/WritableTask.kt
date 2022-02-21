package com.hallett.taskassistant.ui.model.task

import com.hallett.scopes.model.Scope
import com.hallett.domain.TaskStatus

data class WritableTask(
    val taskName: String,
    val scope: Scope?,
    val status: com.hallett.domain.TaskStatus = com.hallett.domain.TaskStatus.INCOMPLETE
)
