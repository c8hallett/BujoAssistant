package com.hallett.taskassistant.ui.model

import com.hallett.domain.model.Task

sealed class TaskView {
    data class HeaderHolder(val text: String) : TaskView()
    data class TaskHolder(
        val task: Task,
        val actions: List<TaskAction>
    ) : TaskView()
}

enum class TaskAction {
    DEFER,
    DELETE,
    COMPLETE,
    UNCOMPLETE,
    RESCHEDULE
}
