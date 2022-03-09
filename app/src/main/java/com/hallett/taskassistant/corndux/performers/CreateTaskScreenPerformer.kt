package com.hallett.taskassistant.corndux.performers

import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.reducers.UpdateCreateTaskScopeSelectionInfo
import com.hallett.taskassistant.corndux.reducers.UpdateCreateTaskSelectedScope
import com.hallett.taskassistant.corndux.performers.actions.CancelScopeSelection
import com.hallett.taskassistant.corndux.performers.actions.EnterScopeSelection
import com.hallett.taskassistant.corndux.performers.actions.SelectNewScope
import com.hallett.taskassistant.corndux.performers.actions.SelectNewScopeType
import com.hallett.taskassistant.corndux.performers.actions.CancelTask
import com.hallett.taskassistant.corndux.performers.actions.SubmitTask
import com.hallett.taskassistant.corndux.sideeffects.NavigateUp
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class CreateTaskScreenPerformer(
    private val taskRepo: ITaskRepository,
    private val ssiGenerator: ScopeSelectionInfoGenerator,
): IPerformer {

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit,
    ) {
        if(state.session.screen is TaskNavDestination.CreateTask) {
            val createTaskState = state.components.createTask
            when(action) {
                is SubmitTask -> {
                    taskRepo.createNewTask(action.taskName, createTaskState.scope)
                    dispatchSideEffect(NavigateUp)
                }
                is CancelTask -> dispatchSideEffect(NavigateUp)
                is EnterScopeSelection -> {
                    val scopeSelectionInfo = ssiGenerator.generateInfo(createTaskState.scope?.type ?: ScopeType.DAY)
                    dispatchCommit(
                        UpdateCreateTaskScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                    )
                }
                is CancelScopeSelection -> {
                    dispatchCommit(
                        UpdateCreateTaskScopeSelectionInfo(scopeSelectionInfo = null)
                    )
                }
                is SelectNewScope -> {
                    dispatchCommit(
                        UpdateCreateTaskSelectedScope(scope = action.newTaskScope)
                    )
                    dispatchCommit(
                        UpdateCreateTaskScopeSelectionInfo(scopeSelectionInfo = null)
                    )
                }
                is SelectNewScopeType -> {
                    val scopeSelectionInfo = ssiGenerator.generateInfo(action.scopeType)
                    dispatchCommit(
                        UpdateCreateTaskScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                    )
                }
            }
        }
    }
}