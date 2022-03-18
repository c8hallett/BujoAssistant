package com.hallett.taskassistant.createTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.performers.actions.CancelScopeSelection
import com.hallett.taskassistant.corndux.performers.actions.EnterScopeSelection
import com.hallett.taskassistant.corndux.performers.actions.SelectNewScope
import com.hallett.taskassistant.corndux.performers.actions.SelectNewScopeType
import com.hallett.taskassistant.corndux.performers.utils.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.corndux.sideeffects.NavigateUp

class CreateTaskPerformer(
    private val taskRepo: ITaskRepository,
    private val ssiGenerator: ScopeSelectionInfoGenerator
):  StatefulPerformer<CreateTaskState> {
    override suspend fun performAction(
        state: CreateTaskState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when(action) {
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
                    UpdateSelectedScopeInfo(selectionInfo = scopeSelectionInfo)
                )
            }
            is CancelScopeSelection -> {
                dispatchCommit(
                    UpdateSelectedScopeInfo(selectionInfo = null)
                )
            }
            is SelectNewScope -> {
                dispatchCommit(
                    UpdateSelectedScope(scope = action.newTaskScope)
                )
                dispatchCommit(
                    UpdateSelectedScopeInfo(selectionInfo = null)
                )
            }
            is SelectNewScopeType -> {
                val scopeSelectionInfo = ssiGenerator.generateInfo(action.scopeType)
                dispatchCommit(
                    UpdateSelectedScopeInfo(selectionInfo = scopeSelectionInfo)
                )
            }
        }
    }
}