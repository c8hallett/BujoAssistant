package com.hallett.taskassistant.corndux.performers

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.performers.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.reducers.UpdateOverdueTaskList
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import java.time.LocalDate

class OverdueTaskPerformer(private val taskRepository: ITaskRepository): IPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchCommit: (Commit) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        if(state.session.screen is TaskNavDestination.OverdueTasks) {
            when(action) {
                is PerformInitialSetup -> {
                    val newOverdueTaskList = taskRepository.getOverdueTasks(pagingConfig, LocalDate.now())
                    dispatchCommit(
                        UpdateOverdueTaskList(taskList = newOverdueTaskList)
                    )
                }
                is AddRandomOverdueTask -> {
                    taskRepository.randomTask(ScopeType.values().random(), overdue = true)
                }
            }
        }
    }
}