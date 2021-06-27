package com.hallett.bujoass.presentation.ui.view_task

import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.presentation.model.PScope

data class ViewableTask(
    val taskName: String,
    val scope: PScope,
    val scopeLabel: String,
    val status: TaskStatus,
    val isCurrent: Boolean
)