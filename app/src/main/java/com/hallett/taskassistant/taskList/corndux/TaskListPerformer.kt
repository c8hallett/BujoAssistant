package com.hallett.taskassistant.taskList.corndux

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.performers.actions.CancelScopeSelection
import com.hallett.taskassistant.corndux.performers.actions.ClickNewScope
import com.hallett.taskassistant.corndux.performers.actions.EnterScopeSelection
import com.hallett.taskassistant.corndux.performers.actions.TaskListAction
import com.hallett.taskassistant.corndux.performers.utils.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.corndux.performers.utils.TaskListTransformer
import com.hallett.taskassistant.createTasks.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.createTasks.corndux.UpdateSelectedScope
import com.hallett.taskassistant.dashboard.corndux.UpdateExpandedTask
import com.hallett.taskassistant.overdueTasks.corndux.UpdateTaskList

class TaskListPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val ssiGenerator: ScopeSelectionInfoGenerator,
    private val transformer: TaskListTransformer,
): StatefulPerformer<TaskListState> {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskListState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> dispatchAction(
                ClickNewScope(scopeCalculator.generateScope(ScopeType.DAY))
            )
            is EnterScopeSelection ->{
                val scopeSelectionInfo =
                    ssiGenerator.generateInfo(state.scope?.type ?: ScopeType.DAY)
                dispatchCommit(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is CancelScopeSelection -> dispatchCommit(
                UpdateScopeSelectionInfo(scopeSelectionInfo = null)
            )
            is TaskListAction.SelectNewScope -> {
                dispatchCommit(
                    UpdateSelectedScope(scope = action.newTaskScope, scopeSelectionInfo = null)
                )
                dispatchCommit(
                    UpdateTaskList(
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
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is TaskListAction.ClickTaskInList -> dispatchCommit(UpdateExpandedTask(action.task))
        }
    }
}