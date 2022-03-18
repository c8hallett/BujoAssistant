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
import com.hallett.taskassistant.corndux.CancelScopeSelection
import com.hallett.taskassistant.corndux.ClickNewScope
import com.hallett.taskassistant.corndux.ClickNewScopeType
import com.hallett.taskassistant.corndux.ClickTaskInList
import com.hallett.taskassistant.corndux.EnterScopeSelection
import com.hallett.taskassistant.corndux.UpdateExpandedTask
import com.hallett.taskassistant.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.corndux.UpdateSelectedScope
import com.hallett.taskassistant.corndux.UpdateTaskList
import com.hallett.taskassistant.corndux.utils.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.corndux.utils.TaskListTransformer

class TaskListPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val ssiGenerator: ScopeSelectionInfoGenerator,
    private val transformer: TaskListTransformer,
) : StatefulPerformer<TaskListState> {

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
            is EnterScopeSelection -> {
                val scopeSelectionInfo =
                    ssiGenerator.generateInfo(state.scope?.type ?: ScopeType.DAY)
                dispatchCommit(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is CancelScopeSelection -> dispatchCommit(
                UpdateScopeSelectionInfo(scopeSelectionInfo = null)
            )
            is ClickNewScope -> {
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
            is ClickNewScopeType -> {
                val scopeSelectionInfo = ssiGenerator.generateInfo(action.scopeType)
                dispatchCommit(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is ClickTaskInList -> dispatchCommit(UpdateExpandedTask(action.task))
        }
    }
}