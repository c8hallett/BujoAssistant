package com.hallett.taskassistant.corndux.performers

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.LoadLargerScope
import com.hallett.taskassistant.corndux.performers.actions.LoadSmallerScope
import com.hallett.taskassistant.corndux.performers.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.reducers.UpdateDashboardTaskList
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import java.time.LocalDate

class DashboardScreenPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator
    ): IPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchCommit: (Commit) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit,
    ) {
        if(state.session.screen !is TaskNavDestination.Dashboard) return

        val dashboardState = state.components.dashboard

        when(action) {
            is PerformInitialSetup -> {
                val currentScope = scopeCalculator.generateScope(dashboardState.scopeType, LocalDate.now())

                dispatchCommit(
                    UpdateDashboardTaskList(
                        scopeType = dashboardState.scopeType,
                        taskList = taskRepo.observeTasksForScope(pagingConfig, currentScope)
                    )
                )
            }
            is LoadLargerScope -> {
                val nextScopeType = dashboardState.scopeType.next()
                if(nextScopeType != dashboardState.scopeType) {
                    val nextScope = scopeCalculator.generateScope(nextScopeType, LocalDate.now())

                    dispatchCommit(
                        UpdateDashboardTaskList(
                            scopeType = nextScopeType,
                            taskList = taskRepo.observeTasksForScope(pagingConfig, nextScope)
                        )
                    )
                }
            }
            is LoadSmallerScope -> {
                val prevScopeType = dashboardState.scopeType.previous()
                if(prevScopeType != dashboardState.scopeType) {
                    val prevScope = scopeCalculator.generateScope(prevScopeType, LocalDate.now())

                    dispatchCommit(
                        UpdateDashboardTaskList(
                            scopeType = prevScopeType,
                            taskList = taskRepo.observeTasksForScope(pagingConfig, prevScope)
                        )
                    )
                }
            }
        }
    }

    private fun ScopeType.previous(): ScopeType = ScopeType.values().run{
        getOrElse(ordinal - 1) { first() }
    }

    private fun ScopeType.next(): ScopeType = ScopeType.values().run {
        getOrElse(ordinal + 1) { last() }
    }
}