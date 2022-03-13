package com.hallett.taskassistant.corndux.performers

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
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
import com.hallett.taskassistant.corndux.performers.actions.TaskClickedInList
import com.hallett.taskassistant.corndux.performers.utils.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.corndux.performers.utils.TaskListTransformer
import com.hallett.taskassistant.corndux.reducers.UpdateTaskListCurrentlySelectedTask
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import kotlinx.coroutines.flow.map

class TaskListPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val ssiGenerator: ScopeSelectionInfoGenerator,
    private val transformer: TaskListTransformer,
): IPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        val taskListState = state.components.taskList
        when(action) {
            is Init -> dispatchAction(
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
                        taskList = transformer.transform(
                            tasks = taskRepo.observeTasksForScope(pagingConfig, action.newTaskScope),
                            includeHeaders = false
                        )
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
        }
    }
}