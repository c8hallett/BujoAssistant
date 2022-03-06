package com.hallett.taskassistant.corndux.actionperformers

import androidx.paging.PagingConfig
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.DashboardState
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.LoadLargerScope
import com.hallett.taskassistant.corndux.LoadSmallerScope
import com.hallett.taskassistant.corndux.PerformInitialSetup
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import java.time.LocalDate

class DashboardActionPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator
    ): IActionPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)
    override suspend fun performAction(
        action: TaskAssistantAction,
        state: TaskAssistantState,
        dispatchNewAction: (TaskAssistantAction) -> Unit,
        dispatchSideEffect: (TaskAssistantSideEffect) -> Unit
    ): TaskAssistantState {
        if(state.session.screen !is TaskNavDestination.Dashboard) return state
        val dashboardState = state.components.dashboard

        return when(action) {
            is PerformInitialSetup -> {
                val currentScope = scopeCalculator.generateScope(dashboardState.scopeType, LocalDate.now())
                state.updateDashboard { copy(taskList = taskRepo.observeTasksForScope(pagingConfig, currentScope)) }
            }
            is LoadLargerScope -> {
                when(val nextScopeType = dashboardState.scopeType.next()) {
                    dashboardState.scopeType -> state
                    else -> {
                        val nextScope = scopeCalculator.generateScope(nextScopeType, LocalDate.now())
                        state.updateDashboard {
                            copy(
                                scopeType = nextScopeType,
                                taskList = taskRepo.observeTasksForScope(pagingConfig, nextScope)
                            )
                        }
                    }
                }
            }
            is LoadSmallerScope -> {
                when(val prevScopeType = dashboardState.scopeType.previous()) {
                    dashboardState.scopeType -> state
                    else -> {
                        val prevScope = scopeCalculator.generateScope(prevScopeType, LocalDate.now())
                        state.updateDashboard {
                            copy(
                                scopeType = prevScopeType,
                                taskList = taskRepo.observeTasksForScope(pagingConfig, prevScope)
                            )
                        }
                    }
                }
            }
            else -> state
        }
    }

    private fun ScopeType.previous(): ScopeType = ScopeType.values().run{
        getOrElse(ordinal - 1) { first() }
    }

    private fun ScopeType.next(): ScopeType = ScopeType.values().run {
        getOrElse(ordinal + 1) { last() }
    }

    private inline fun TaskAssistantState.updateDashboard( update: DashboardState.() -> DashboardState ): TaskAssistantState {
        return updateComponents { copy(dashboard = dashboard.update()) }
    }
}