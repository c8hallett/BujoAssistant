package com.hallett.domain

import com.hallett.scopes.model.Scope

data class Task(
    val id: Long,
    val taskName: String,
    val scope: Scope?,
    val status: TaskStatus,
) {
    companion object {
        val DEFAULT_VALUE = Task(
            id = 0L,
            taskName = "",
            scope = null,
            status = TaskStatus.INCOMPLETE,
        )
    }
}
