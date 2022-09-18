package com.hallett.taskassistant.features.createTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.CancelTask
import com.hallett.taskassistant.corndux.ClearCreateTaskState
import com.hallett.taskassistant.corndux.NavigateUp
import com.hallett.taskassistant.corndux.SubmitTask
import com.hallett.taskassistant.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.corndux.UpdateSelectedScope
import com.hallett.taskassistant.ui.composables.CancelScopeSelection
import com.hallett.taskassistant.ui.composables.ClickNewScope
import com.hallett.taskassistant.ui.composables.ClickNewScopeType
import com.hallett.taskassistant.ui.composables.EnterScopeSelection
import com.hallett.taskassistant.util.ScopeSelectionInfoGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CreateTaskPerformer(
    private val taskRepo: ITaskRepository,
    private val ssiGenerator: ScopeSelectionInfoGenerator,
    private val workScope: CoroutineScope
) : StatefulPerformer<CreateTaskState> {
    override fun performAction(
        state: CreateTaskState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when (action) {
            is SubmitTask -> {
                withRepo {
                    createNewTask(action.taskName, state.scope)
                    dispatchAction(ClearCreateTaskState)
                    dispatchSideEffect(NavigateUp)
                }
            }
            is CancelTask -> dispatchSideEffect(NavigateUp)
            is EnterScopeSelection -> {
                val scopeSelectionInfo =
                    ssiGenerator.generateInfo(state.scope?.type ?: ScopeType.DAY)
                dispatchAction(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
            is CancelScopeSelection -> {
                dispatchAction(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = null)
                )
            }
            is ClickNewScope -> dispatchAction(
                UpdateSelectedScope(scope = action.newTaskScope, scopeSelectionInfo = null)
            )
            is ClickNewScopeType -> {
                val scopeSelectionInfo = ssiGenerator.generateInfo(action.scopeType)
                dispatchAction(
                    UpdateScopeSelectionInfo(scopeSelectionInfo = scopeSelectionInfo)
                )
            }
        }
    }

    private inline fun withRepo(crossinline operation: suspend ITaskRepository.() -> Unit) =
        workScope.launch {
            taskRepo.operation()
        }
}