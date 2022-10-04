package com.hallett.taskassistant.ui.genericTaskList

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatelessPerformer
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.main.TaskNavDestination
import com.hallett.taskassistant.main.corndux.NavigateSingleTop
import com.hallett.taskassistant.main.corndux.UpdateExpandedTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TaskActionsPerformer(
    private val workScope: CoroutineScope,
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
) : StatelessPerformer {
    override fun performAction(
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {

        when (action) {
            !is TaskAction -> {}
            is ClickRescheduleTask, CancelRescheduleTask -> {} // these passes through
            is ClickDeleteTask -> withRepo { deleteTask(action.task) }
            is ClickDeferTask -> {
                val nextScope = action.task.scope?.let {
                    scopeCalculator.add(it, 1)
                }
                withRepo { moveToNewScope(action.task, nextScope) }
            }
            is ClickMarkTaskAsComplete -> withRepo {
                moveToNewScope(action.task, scopeCalculator.generateScope())
                updateStatus(action.task, TaskStatus.COMPLETE)
            }
            is SubmitRescheduleTask -> withRepo {
                moveToNewScope(action.task, action.newScope)
            }
            is ClickMarkTaskAsIncomplete -> withRepo {
                updateStatus(
                    action.task,
                    TaskStatus.INCOMPLETE
                )
            }
            is ClickTaskInList -> {
                dispatchAction(UpdateExpandedTask(action.task))
            }
            is ClickEditTask -> {
                val route = TaskNavDestination.CreateTask.createRoute(action.task.id)
                dispatchSideEffect(NavigateSingleTop(route))
            }
        }
    }

    private inline fun withRepo(crossinline operation: suspend ITaskRepository.() -> Unit) =
        workScope.launch {
            taskRepo.operation()
        }
}