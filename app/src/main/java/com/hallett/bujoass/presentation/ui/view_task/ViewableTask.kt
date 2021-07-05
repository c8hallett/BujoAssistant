package com.hallett.bujoass.presentation.ui.view_task

import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance

data class ViewableTask(
    val taskName: String,
    val scope: PScopeInstance,
    val scopeLabel: String,
    val status: TaskStatus,
    val isCurrent: Boolean
)