package com.hallett.taskassistant.features.createTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.Task
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.features.scopeSelection.CancelScopeSelection
import com.hallett.taskassistant.features.scopeSelection.ClickNewScope
import com.hallett.taskassistant.features.scopeSelection.ClickNewScopeType
import com.hallett.taskassistant.features.scopeSelection.EnterScopeSelection
import com.hallett.taskassistant.features.scopeSelection.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.main.corndux.CancelTask
import com.hallett.taskassistant.main.corndux.ClearCreateTaskState
import com.hallett.taskassistant.main.corndux.DisplayTaskForEdit
import com.hallett.taskassistant.main.corndux.NavigateUp
import com.hallett.taskassistant.main.corndux.OpenTask
import com.hallett.taskassistant.main.corndux.SubmitTask
import com.hallett.taskassistant.main.corndux.UpdateScopeSelectionInfo
import com.hallett.taskassistant.main.corndux.UpdateSelectedScope
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
            is OpenTask -> {
                when(val taskId = action.taskId) {
                    null -> dispatchAction(ClearCreateTaskState)
                    else -> withRepo {
                        when(val task = getTask(taskId)) {
                            null -> dispatchAction(ClearCreateTaskState)
                            else -> dispatchAction(DisplayTaskForEdit(task))
                        }
                    }
                }
            }
            is SubmitTask -> {
                withRepo {
                    upsert(
                        taskId = state.taskId,
                        taskName = action.taskName,
                        scope = state.scope
                    )
                    dispatchSideEffect(NavigateUp)
                }
            }
            is CancelTask -> {
                dispatchSideEffect(NavigateUp)
            }
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