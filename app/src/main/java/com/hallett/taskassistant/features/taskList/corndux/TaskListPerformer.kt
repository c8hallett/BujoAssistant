package com.hallett.taskassistant.features.taskList.corndux

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.corndux.UpdateSelectedScope
import com.hallett.taskassistant.corndux.UpdateTaskList
import com.hallett.taskassistant.ui.composables.CancelScopeSelection
import com.hallett.taskassistant.ui.composables.ClickNewScope
import com.hallett.taskassistant.ui.composables.ClickNewScopeType
import com.hallett.taskassistant.ui.composables.EnterScopeSelection
import com.hallett.taskassistant.util.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.util.TaskListTransformer

class TaskListPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val ssiGenerator: ScopeSelectionInfoGenerator,
    private val transformer: TaskListTransformer,
) : StatefulPerformer<TaskListState> {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override fun performAction(
        state: TaskListState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> dispatchAction(
                ClickNewScope(scopeCalculator.generateScope(ScopeType.DAY))
            )
            is EnterScopeSelection -> {
                val scopeSelectionInfo =
                    ssiGenerator.generateInfo(state.scope?.type ?: ScopeType.DAY)
                dispatchAction(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is CancelScopeSelection -> dispatchAction(
                UpdateScopeSelectionInfo(scopeSelectionInfo = null)
            )
            is ClickNewScope -> {
                dispatchAction(
                    UpdateSelectedScope(scope = action.newTaskScope, scopeSelectionInfo = null)
                )
                dispatchAction(
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
                dispatchAction(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
        }
    }
}