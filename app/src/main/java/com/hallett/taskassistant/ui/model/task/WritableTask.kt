package com.hallett.taskassistant.ui.model.task

import com.hallett.scopes.model.Scope
import com.hallett.domain.model.TaskStatus

data class WritableTask(
    val taskName: String,
    val scope: Scope?,
    val status: TaskStatus = TaskStatus.INCOMPLETE
)
