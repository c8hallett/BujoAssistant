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
import com.hallett.taskassistant.corndux.performers.actions.TaskListAction
import com.hallett.taskassistant.corndux.performers.utils.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.corndux.performers.utils.TaskListTransformer
import com.hallett.taskassistant.corndux.reducers.UpdateTaskListExpandedTask
import com.hallett.taskassistant.corndux.reducers.UpdateTaskListScopeSelectionInfo
import com.hallett.taskassistant.corndux.reducers.UpdateTaskListSelectedScope
import com.hallett.taskassistant.corndux.reducers.UpdateTaskListTaskList

class TaskListPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val ssiGenerator: ScopeSelectionInfoGenerator,
    private val transformer: TaskListTransformer,
) : IPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        val taskListState = state.components.taskList
        when (action) {
            is Init -> dispatchAction(
                TaskListAction.SelectNewScope(scopeCalculator.generateScope(ScopeType.DAY))
            )
            !is TaskListAction -> {}
            is TaskListAction.EnterScopeSelection -> {
                val scopeSelectionInfo =
                    ssiGenerator.generateInfo(taskListState.scope?.type ?: ScopeType.DAY)
                dispatchCommit(
                    UpdateTaskListScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is TaskListAction.CancelScopeSelection -> dispatchCommit(
                UpdateTaskListScopeSelectionInfo(scopeSelectionInfo = null)
            )
            is TaskListAction.SelectNewScope -> {
                dispatchCommit(
                    UpdateTaskListSelectedScope(scope = action.newTaskScope)
                )
                dispatchCommit(
                    UpdateTaskListScopeSelectionInfo(scopeSelectionInfo = null)
                )
                dispatchCommit(
                    UpdateTaskListTaskList(
                        taskList = transformer.transform(
                            tasks = taskRepo.observeTasksForScope(
                                pagingConfig,
                                action.newTaskScope
                            ),
                            includeHeaders = false
                        )
                    )
                )
            }
            is TaskListAction.SelectNewScopeType -> {
                val scopeSelectionInfo = ssiGenerator.generateInfo(action.scopeType)
                dispatchCommit(
                    UpdateTaskListScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is TaskListAction.ClickTaskInList -> when (action.task) {
                taskListState.currentlyExpandedTask -> dispatchCommit(
                    UpdateTaskListExpandedTask(task = null)
                )
                else -> dispatchCommit(
                    UpdateTaskListExpandedTask(task = action.task)
                )
            }
        }
    }
}