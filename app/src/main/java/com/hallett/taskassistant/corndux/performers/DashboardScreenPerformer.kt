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
import com.hallett.taskassistant.corndux.performers.actions.DashboardAction
import com.hallett.taskassistant.corndux.performers.actions.LoadLargerScope
import com.hallett.taskassistant.corndux.performers.actions.LoadSmallerScope
import com.hallett.taskassistant.corndux.performers.actions.OverdueTaskAction
import com.hallett.taskassistant.corndux.performers.utils.TaskListTransformer
import com.hallett.taskassistant.corndux.reducers.UpdateDashboardExpandedTask
import com.hallett.taskassistant.corndux.reducers.UpdateDashboardTaskList
import java.time.LocalDate

class DashboardScreenPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val transformer: TaskListTransformer,
) : IPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit,
    ) {
        val dashboardState = state.components.dashboard

        when (action) {
            is Init -> {
                val currentScope = scopeCalculator.generateScope(
                    state.components.dashboard.scopeType,
                    LocalDate.now()
                )
                dispatchCommit(
                    UpdateDashboardTaskList(
                        scopeType = state.components.dashboard.scopeType,
                        taskList = transformer.transform(
                            tasks = taskRepo.observeTasksForScope(pagingConfig, currentScope),
                            includeHeaders = false
                        )
                    )
                )
            }
            !is DashboardAction -> {}
            is DashboardAction.TaskClickedInList -> {
                val newTask = when(action.task){
                    dashboardState.currentlyExpandedTask -> null
                    else -> action.task
                }
                dispatchCommit(
                    UpdateDashboardExpandedTask(newTask)
                )
            }
            is LoadLargerScope -> {
                val nextScopeType = dashboardState.scopeType.next()
                if (nextScopeType != dashboardState.scopeType) {
                    val nextScope = scopeCalculator.generateScope(nextScopeType, LocalDate.now())

                    dispatchCommit(
                        UpdateDashboardTaskList(
                            scopeType = nextScopeType,
                            taskList = transformer.transform(
                                tasks = taskRepo.observeTasksForScope(pagingConfig, nextScope),
                                includeHeaders = false
                            )
                        )
                    )
                }
            }
            is LoadSmallerScope -> {
                val prevScopeType = dashboardState.scopeType.previous()
                if (prevScopeType != dashboardState.scopeType) {
                    val prevScope = scopeCalculator.generateScope(prevScopeType, LocalDate.now())

                    dispatchCommit(
                        UpdateDashboardTaskList(
                            scopeType = prevScopeType,
                            taskList = transformer.transform(
                                tasks = taskRepo.observeTasksForScope(pagingConfig, prevScope),
                                includeHeaders = false
                            )
                        )
                    )
                }
            }
        }
    }

    private fun ScopeType.previous(): ScopeType = ScopeType.values().run {
        getOrElse(ordinal - 1) { first() }
    }

    private fun ScopeType.next(): ScopeType = ScopeType.values().run {
        getOrElse(ordinal + 1) { last() }
    }
}