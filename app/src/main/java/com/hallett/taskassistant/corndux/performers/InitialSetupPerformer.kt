package com.hallett.taskassistant.corndux.performers

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.Task
import com.hallett.logging.logI
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.SelectNewScope
import com.hallett.taskassistant.corndux.reducers.UpdateDashboardTaskList
import com.hallett.taskassistant.corndux.reducers.UpdateFutureTaskLists
import com.hallett.taskassistant.corndux.reducers.UpdateOverdueTaskList
import com.hallett.taskassistant.ui.formatters.Formatter
import com.hallett.taskassistant.ui.model.TaskView
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InitialSetupPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val scopeLabelFormatter: Formatter<Scope?, String>
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

            val unscheduledList: Flow<PagingData<TaskView>> = taskRepo.observeTasksForScope(pagingConfig, null, false)
                .map { pagedData -> pagedData.map {TaskView.TaskHolder(it) } }
            val scheduledList = taskRepo.observeFutureTasks(pagingConfig, LocalDate.now())
                .map { pagedData -> pagedData.toUiModel() }
            dispatchCommit(
                UpdateFutureTaskLists(unscheduled = unscheduledList, scheduled = scheduledList)
            )
        }
    }

    private fun PagingData<Task>.toUiModel(): PagingData<TaskView> = map {
        TaskView.TaskHolder(it)
    }.insertSeparators{ task1, task2 ->
        when {
            task2 == null -> null
            task1?.task?.scope != task2.task.scope -> TaskView.HeaderHolder(scopeLabelFormatter.format(task2.task.scope))
            else -> null
        }
    }
}