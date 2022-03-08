package com.hallett.taskassistant.corndux.performers

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.SelectNewScope
import com.hallett.taskassistant.corndux.reducers.UpdateDashboardTaskList
import com.hallett.taskassistant.corndux.reducers.UpdateOverdueTaskList
import java.time.LocalDate

class InitialSetupPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,

    ): IPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        if(action is Init) {
            // send off initial task list update
            dispatchAction(
                SelectNewScope(scopeCalculator.generateScope(ScopeType.DAY))
            )

            // Send off initial dashboard update
            val currentScope = scopeCalculator.generateScope(state.components.dashboard.scopeType, LocalDate.now())
            dispatchCommit(
                UpdateDashboardTaskList(
                    scopeType = state.components.dashboard.scopeType,
                    taskList = taskRepo.observeTasksForScope(pagingConfig, currentScope)
                )
            )

            // Send off initial overdue task list update
            val newOverdueTaskList = taskRepo.getOverdueTasks(pagingConfig, LocalDate.now())
            dispatchCommit(
                UpdateOverdueTaskList(taskList = newOverdueTaskList)
            )
        }
    }
}