package com.hallett.taskassistant.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import kotlinx.coroutines.flow.Flow

data class TaskAssistantState(
    val scope: Scope?,
    val scopeSelectionInfo: ScopeSelectionInfo?,
    val tasks: Flow<PagingData<Task>>?,
    val error: Error?
): IState

data class ScopeSelectionInfo(
    val scopeType: ScopeType,
    val scopes: Flow<PagingData<Scope>>
)

enum class ScreenContext {
    OVERDUE_TASKS,
    CURRENT_TASKS,
    CREATE_TASK
}

sealed class Error(message: String) {
    object EmptyTaskName: Error("Task name cannot be blank.")
}
