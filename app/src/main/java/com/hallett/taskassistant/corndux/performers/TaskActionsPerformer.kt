package com.hallett.taskassistant.corndux.performers

import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Performer
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.UpdateExpandedTask
import com.hallett.taskassistant.ui.composables.ClickTaskInList
import com.hallett.taskassistant.ui.composables.DeferTask
import com.hallett.taskassistant.ui.composables.DeleteTask
import com.hallett.taskassistant.ui.composables.MarkTaskAsComplete
import com.hallett.taskassistant.ui.composables.MarkTaskAsIncomplete
import com.hallett.taskassistant.ui.composables.RescheduleTask
import com.hallett.taskassistant.ui.composables.TaskAction

class TaskActionsPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
) : Performer {
    override suspend fun performAction(
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when (action) {
            !is TaskAction -> {}
            is DeleteTask -> taskRepo.deleteTask(action.task)
            is DeferTask -> {
                val nextScope = action.task.scope?.let {
                    scopeCalculator.add(it, 1)
                }
                taskRepo.moveToNewScope(action.task, nextScope)
            }
            is RescheduleTask -> {} // trigger side effect to show dialog and prompt for new scope
            //  taskRepo.moveToNewScope(action.task, taskListState.scope)
            is MarkTaskAsComplete -> {
                taskRepo.moveToNewScope(action.task, scopeCalculator.generateScope())
                taskRepo.updateStatus(action.task, TaskStatus.COMPLETE)
            }
            is MarkTaskAsIncomplete -> taskRepo.updateStatus(action.task, TaskStatus.INCOMPLETE)
            is ClickTaskInList -> dispatchCommit(UpdateExpandedTask(action.task))
        }
    }
}