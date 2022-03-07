package com.hallett.taskassistant.corndux.performers

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.reducers.UpdateTaskListScopeSelectionInfo
import com.hallett.taskassistant.corndux.reducers.UpdateTaskListSelectedScope
import com.hallett.taskassistant.corndux.reducers.UpdateTaskListTaskList
import com.hallett.taskassistant.corndux.performers.actions.CancelScopeSelection
import com.hallett.taskassistant.corndux.performers.actions.EnterScopeSelection
import com.hallett.taskassistant.corndux.performers.actions.SelectNewScope
import com.hallett.taskassistant.corndux.performers.actions.SelectNewScopeType
import com.hallett.taskassistant.corndux.performers.actions.DeferTask
import com.hallett.taskassistant.corndux.performers.actions.DeleteTask
import com.hallett.taskassistant.corndux.performers.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.performers.actions.RescheduleTask
import com.hallett.taskassistant.corndux.performers.actions.TaskClickedInList
import com.hallett.taskassistant.corndux.performers.actions.ToggleTaskComplete
import com.hallett.taskassistant.corndux.reducers.UpdateTaskListCurrentlySelectedTask
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class TaskListPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val ssiGenerator: ScopeSelectionInfoGenerator,
): IPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchCommit: (Commit) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        if (state.session.screen !is TaskNavDestination.TaskList) return
        val taskListState = state.components.taskList
        when(action) {
            is PerformInitialSetup -> dispatchAction(
                SelectNewScope(scopeCalculator.generateScope(ScopeType.DAY))
            )
            is EnterScopeSelection -> {
                val scopeSelectionInfo = ssiGenerator.generateInfo(taskListState.scope?.type ?: ScopeType.DAY)
                dispatchCommit(
                    UpdateTaskListScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is CancelScopeSelection -> dispatchCommit(
                UpdateTaskListScopeSelectionInfo(scopeSelectionInfo = null)
            )
            is SelectNewScope -> {
                dispatchCommit(
                    UpdateTaskListSelectedScope(scope = action.newTaskScope)
                )
                dispatchCommit(
                    UpdateTaskListScopeSelectionInfo(scopeSelectionInfo = null)
                )
                dispatchCommit(
                    UpdateTaskListTaskList(
                        taskList = taskRepo.observeTasksForScope(pagingConfig, action.newTaskScope)
                    )
                )
            }
            is SelectNewScopeType -> {
                val scopeSelectionInfo = ssiGenerator.generateInfo(action.scopeType)
                dispatchCommit(
                    UpdateTaskListScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is TaskClickedInList -> when(action.task){
                taskListState.currentlyExpandedTask -> dispatchCommit(
                    UpdateTaskListCurrentlySelectedTask(task = null)
                )
                else -> dispatchCommit(
                    UpdateTaskListCurrentlySelectedTask(task = action.task)
                )
            }
            is DeleteTask -> taskRepo.deleteTask(action.task)
            is DeferTask -> {
                val nextScope = when(val oldScope = action.task.scope) {
                    null -> null
                    else -> scopeCalculator.add(oldScope, 1)
                }
                taskRepo.moveToNewScope(action.task, nextScope)
            }
            is RescheduleTask -> taskRepo.moveToNewScope(action.task, taskListState.scope)
            is ToggleTaskComplete -> when(action.task.status) {
                TaskStatus.COMPLETE -> taskRepo.updateStatus(action.task, TaskStatus.INCOMPLETE)
                TaskStatus.INCOMPLETE -> {
                    taskRepo.moveToNewScope(action.task, scopeCalculator.generateScope())
                    taskRepo.updateStatus(action.task, TaskStatus.COMPLETE)
                }
            }
        }
    }
}