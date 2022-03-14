package com.hallett.taskassistant.corndux.performers

import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.CancelTask
import com.hallett.taskassistant.corndux.performers.actions.CreateTaskAction
import com.hallett.taskassistant.corndux.performers.actions.SubmitTask
import com.hallett.taskassistant.corndux.performers.utils.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.corndux.reducers.UpdateCreateTaskScopeSelectionInfo
import com.hallett.taskassistant.corndux.reducers.UpdateCreateTaskSelectedScope
import com.hallett.taskassistant.corndux.sideeffects.NavigateUp

class CreateTaskScreenPerformer(
    private val taskRepo: ITaskRepository,
    private val ssiGenerator: ScopeSelectionInfoGenerator,
) : IPerformer {

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit,
    ) {
        val createTaskState = state.components.createTask
        when (action) {
            !is CreateTaskAction -> {}
            is SubmitTask -> {
                taskRepo.createNewTask(action.taskName, createTaskState.scope)
                dispatchSideEffect(NavigateUp)
            }
            is CancelTask -> dispatchSideEffect(NavigateUp)
            is CreateTaskAction.EnterScopeSelection -> {
                val scopeSelectionInfo =
                    ssiGenerator.generateInfo(createTaskState.scope?.type ?: ScopeType.DAY)
                dispatchCommit(
                    UpdateCreateTaskScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is CreateTaskAction.CancelScopeSelection -> {
                dispatchCommit(
                    UpdateCreateTaskScopeSelectionInfo(scopeSelectionInfo = null)
                )
            }
            is CreateTaskAction.SelectNewScope -> {
                dispatchCommit(
                    UpdateCreateTaskSelectedScope(scope = action.newTaskScope)
                )
                dispatchCommit(
                    UpdateCreateTaskScopeSelectionInfo(scopeSelectionInfo = null)
                )
            }
            is CreateTaskAction.SelectNewScopeType -> {
                val scopeSelectionInfo = ssiGenerator.generateInfo(action.scopeType)
                dispatchCommit(
                    UpdateCreateTaskScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
        }
    }
}