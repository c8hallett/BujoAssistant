package com.hallett.taskassistant.corndux.performers

import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.DeferTask
import com.hallett.taskassistant.corndux.performers.actions.DeleteTask
import com.hallett.taskassistant.corndux.performers.actions.MarkTaskAsComplete
import com.hallett.taskassistant.corndux.performers.actions.MarkTaskAsIncomplete
import com.hallett.taskassistant.corndux.performers.actions.RescheduleTask
import com.hallett.taskassistant.corndux.performers.actions.TaskAction

class TaskActionPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
): IPerformer {
    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when(action) {
            !is TaskAction -> {}
            is DeleteTask -> taskRepo.deleteTask(action.task)
            is DeferTask ->  {
                val nextScope = action.task.scope?.let {
                    scopeCalculator.add(it, 1)
                }
                taskRepo.moveToNewScope(action.task, nextScope)
            }
            is RescheduleTask -> {} // trigger side effect to show dialog and prompt for new scope
            //  taskRepo.moveToNewScope(action.task, taskListState.scope)
            is MarkTaskAsComplete ->  {
                taskRepo.moveToNewScope(action.task, scopeCalculator.generateScope())
                taskRepo.updateStatus(action.task, TaskStatus.COMPLETE)
            }
            is MarkTaskAsIncomplete -> taskRepo.updateStatus(action.task, TaskStatus.INCOMPLETE)
        }
    }
}