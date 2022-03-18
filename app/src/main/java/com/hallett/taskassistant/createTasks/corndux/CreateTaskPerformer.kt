package com.hallett.taskassistant.createTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.CancelTask
import com.hallett.taskassistant.corndux.ClearCreateTaskState
import com.hallett.taskassistant.corndux.SubmitTask
import com.hallett.taskassistant.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.corndux.UpdateSelectedScope
import com.hallett.taskassistant.corndux.sideeffects.NavigateUp
import com.hallett.taskassistant.corndux.utils.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.ui.composables.CancelScopeSelection
import com.hallett.taskassistant.ui.composables.ClickNewScope
import com.hallett.taskassistant.ui.composables.ClickNewScopeType
import com.hallett.taskassistant.ui.composables.EnterScopeSelection

class CreateTaskPerformer(
    private val taskRepo: ITaskRepository,
    private val ssiGenerator: ScopeSelectionInfoGenerator
) : StatefulPerformer<CreateTaskState> {
    override suspend fun performAction(
        state: CreateTaskState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when (action) {
            is SubmitTask -> {
                taskRepo.createNewTask(action.taskName, state.scope)
                dispatchCommit(ClearCreateTaskState)
                dispatchSideEffect(NavigateUp)
            }
            is CancelTask -> dispatchSideEffect(NavigateUp)
            is EnterScopeSelection -> {
                val scopeSelectionInfo =
                    ssiGenerator.generateInfo(state.scope?.type ?: ScopeType.DAY)
                dispatchCommit(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is CancelScopeSelection -> {
                dispatchCommit(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = null)
                )
            }
            is ClickNewScope -> dispatchCommit(
                UpdateSelectedScope(scope = action.newTaskScope, scopeSelectionInfo = null)
            )
            is ClickNewScopeType -> {
                val scopeSelectionInfo = ssiGenerator.generateInfo(action.scopeType)
                dispatchCommit(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
        }
    }
}