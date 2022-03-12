package com.hallett.taskassistant.ui.model

import com.hallett.domain.model.Task

sealed class TaskView {
    data class HeaderHolder(val text: String): TaskView()
    data class TaskHolder(val task: Task): TaskView()
}