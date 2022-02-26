package com.hallett.taskassistant.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import kotlinx.coroutines.flow.Flow

data class TaskAssistantState(
    val screen: TaskNavDestination,
    val scope: Scope?,
    val scopeSelectionInfo: ScopeSelectionInfo?,
    val tasks: Flow<PagingData<Task>>?,
    val error: Error?
): IState

data class ScopeSelectionInfo(
    val scopeType: ScopeType,
    val scopes: Flow<PagingData<Scope>>
)

sealed class Error(message: String) {
    object EmptyTaskName: Error("Task name cannot be blank.")
}
