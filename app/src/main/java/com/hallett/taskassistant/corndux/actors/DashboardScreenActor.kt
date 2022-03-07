package com.hallett.taskassistant.corndux.actors

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.Task
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.DashboardState
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.IReducer
import com.hallett.taskassistant.corndux.actions.LoadLargerScope
import com.hallett.taskassistant.corndux.actions.LoadSmallerScope
import com.hallett.taskassistant.corndux.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class DashboardScreenActor(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator
    ): IActionPerformer, IReducer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    private data class DisplayNewDashboardList(
        val scopeType: ScopeType,
        val taskList: Flow<PagingData<Task>>
    ): Action

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchNewAction: (Action) -> Unit,
    ) {
        if(state.session.screen !is TaskNavDestination.Dashboard) return

        val dashboardState = state.components.dashboard

        when(action) {
            is PerformInitialSetup -> {
                val currentScope = scopeCalculator.generateScope(dashboardState.scopeType, LocalDate.now())

                dispatchNewAction(
                    DisplayNewDashboardList(
                        scopeType = dashboardState.scopeType,
                        taskList = taskRepo.observeTasksForScope(pagingConfig, currentScope)
                    )
                )
            }
            is LoadLargerScope -> {
                val nextScopeType = dashboardState.scopeType.next()
                if(nextScopeType != dashboardState.scopeType) {
                    val nextScope = scopeCalculator.generateScope(nextScopeType, LocalDate.now())

                    dispatchNewAction(
                        DisplayNewDashboardList(
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

                    dispatchNewAction(
                        DisplayNewDashboardList(
                            scopeType = prevScopeType,
                            taskList = taskRepo.observeTasksForScope(pagingConfig, prevScope)
                        )
                    )
                }
            }
        }
    }

    override fun reduce(
        state: TaskAssistantState,
        action: Action,
        dispatchSideEffect: (SideEffect) -> Unit
    ): TaskAssistantState {
        return when(action) {
            is DisplayNewDashboardList -> state.updateDashboard {
                copy(
                    scopeType = action.scopeType,
                    taskList = action.taskList
                )
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

    private inline fun TaskAssistantState.updateDashboard( update: DashboardState.() -> DashboardState): TaskAssistantState {
        return updateComponents { copy(dashboard = dashboard.update()) }
    }
}