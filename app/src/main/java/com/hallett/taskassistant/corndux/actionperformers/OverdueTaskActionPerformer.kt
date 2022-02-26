package com.hallett.taskassistant.corndux.actionperformers

import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class OverdueTaskActionPerformer(private val taskRepository: ITaskRepository): IActionPerformer {
    override suspend fun performAction(
        action: TaskAssistantAction,
        state: TaskAssistantState,
        dispatchNewAction: (TaskAssistantAction) -> Unit,
        dispatchSideEffect: (TaskAssistantSideEffect) -> Unit
    ): TaskAssistantState {
        if(state.screen != TaskNavDestination.OverdueTasks) return state
        return when(action) {
            AddRandomOverdueTask -> {
                taskRepository.randomTask(ScopeType.values().random(), overdue = true)
                state
            }
            else -> state
        }
    }
}