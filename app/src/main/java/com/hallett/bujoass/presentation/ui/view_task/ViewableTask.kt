package com.hallett.bujoass.presentation.ui.view_task

import com.hallett.bujoass.domain.model.TaskStatus

data class ViewableTask(
    val taskName: String,
    val scope: Scope?,
    val scopeLabel: String,
    val status: TaskStatus,
)