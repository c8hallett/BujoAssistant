package com.hallett.taskassistant.features.createTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.Task
import com.hallett.domain.model.TaskStatus
import com.hallett.logging.logI
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.features.scopeSelection.CancelScopeSelection
import com.hallett.taskassistant.features.scopeSelection.ClickNewScope
import com.hallett.taskassistant.features.scopeSelection.ClickNewScopeType
import com.hallett.taskassistant.features.scopeSelection.EnterScopeSelection
import com.hallett.taskassistant.features.scopeSelection.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.main.corndux.CancelTask
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

    private val newTask = Task(
        id = 0L,
        name = "",
        scope = null,
        status = TaskStatus.INCOMPLETE
    )

    override fun performAction(
        state: CreateTaskState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when (action) {
            is OpenTask -> {
                when (val taskId = action.taskId) {
                    null -> dispatchAction(DisplayTaskForEdit(newTask))
                    else -> withRepo {
                        when (val task = getTask(taskId)) {
                            null -> dispatchAction(DisplayTaskForEdit(newTask))
                            else -> dispatchAction(DisplayTaskForEdit(task))
                        }
                    }
                }
            }
            is SubmitTask -> {
                withRepo {
                    val formattedTask = with(state.task) {
                        copy(name = name.trimExtraSpaces())
                    }
                    upsert(formattedTask)
                    logI("Sent task off: $formattedTask")
                    dispatchSideEffect(NavigateUp)
                }
            }
            is CancelTask -> {
                dispatchSideEffect(NavigateUp)
            }
            is EnterScopeSelection -> {
                val scopeSelectionInfo =
                    ssiGenerator.generateInfo(state.task.scope?.type ?: ScopeType.DAY)
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

    private fun String.trimExtraSpaces(): String {
        var foundChar = false
        return this
            .trimStart()
            .foldRightIndexed("") { index, character, acc ->
                when {
                    foundChar -> character + acc
                    character.isWhitespace() -> when {
                        // current character is a double space
                        this.getOrNull(index - 1)?.isWhitespace() == true -> acc
                        else -> {
                            foundChar = true
                            character + acc
                        }
                    }
                    else -> {
                        foundChar = true
                        character + acc
                    }
                }
            }
    }
}