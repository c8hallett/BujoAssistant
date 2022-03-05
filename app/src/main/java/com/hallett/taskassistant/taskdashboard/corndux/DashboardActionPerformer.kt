package com.hallett.taskassistant.taskdashboard.corndux

import androidx.paging.PagingConfig
import com.hallett.database.ITaskRepository
import com.hallett.logging.logI
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import java.time.LocalDate

class DashboardActionPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator
    ): IDashboardActionPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)
    override suspend fun performAction(
        action: DashboardAction,
        state: DashboardState,
        dispatchNewAction: (DashboardAction) -> Unit,
        dispatchSideEffect: (Nothing) -> Unit
    ): DashboardState {
        return when(action) {
            is FetchInitialData -> {
                val currentScope = scopeCalculator.generateScope(state.scopeType, LocalDate.now())
                state.copy(taskList = taskRepo.observeTasksForScope(pagingConfig, currentScope))
            }
            is LoadLargerScope -> {
                when(val nextScopeType = state.scopeType.next()) {
                    state.scopeType -> state
                    else -> {
                        val nextScope = scopeCalculator.generateScope(nextScopeType, LocalDate.now())
                        logI("Fetching scope: $nextScope")
                        state.copy(
                            scopeType = nextScopeType,
                            taskList = taskRepo.observeTasksForScope(pagingConfig, nextScope)
                        )
                    }
                }
            }
            is LoadSmallerScope -> {
                when(val prevScopeType = state.scopeType.previous()) {
                    state.scopeType -> state
                    else -> {
                        val prevScope = scopeCalculator.generateScope(prevScopeType, LocalDate.now())
                        logI("Fetching scope: $prevScope")
                        state.copy(
                            scopeType = prevScopeType,
                            taskList = taskRepo.observeTasksForScope(pagingConfig, prevScope)
                        )
                    }
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