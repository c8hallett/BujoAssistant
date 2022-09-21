package com.hallett.taskassistant.features.genericTaskList

import com.hallett.domain.model.Task

sealed class TaskView {
    data class HeaderHolder(val text: String) : TaskView()
    data class TaskHolder(
        val task: Task,
        val actions: List<TaskActionType>
    ) : TaskView()
}

enum class TaskActionType {
    DEFER,
    DELETE,
    COMPLETE,
    UNCOMPLETE,
    RESCHEDULE,
    EDIT
}
