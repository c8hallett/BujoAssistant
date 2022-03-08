package com.hallett.taskassistant.corndux.performers

import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.AddRandomOverdueTask
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class OverdueTaskPerformer(private val taskRepository: ITaskRepository): IPerformer {

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        if(state.session.screen is TaskNavDestination.OverdueTasks) {
            when(action) {
                is AddRandomOverdueTask -> {
                    taskRepository.randomTask(ScopeType.values().random(), overdue = true)
                }
            }
        }
    }
}