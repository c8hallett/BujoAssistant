package com.hallett.taskassistant.taskdashboard.corndux

import androidx.paging.PagingConfig
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeGenerator
import java.time.LocalDate

class DashboardActionPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeGenerator: IScopeGenerator
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
                val currentScope = scopeGenerator.generateScope(state.scopeType, LocalDate.now())
                state.copy(taskList = taskRepo.observeTasksForScope(pagingConfig, currentScope))
            }
            is LoadLargerScope -> {
                when(val nextScope = state.scopeType.next()) {
                    state.scopeType -> state
                    else -> {
                        val currentScope = scopeGenerator.generateScope(nextScope, LocalDate.now())
                        state.copy(
                            scopeType = nextScope,
                            taskList = taskRepo.observeTasksForScope(pagingConfig, currentScope)
                        )
                    }
                }
            }
            is LoadSmallerScope -> {
                when(val prevScope = state.scopeType.previous()) {
                    state.scopeType -> state
                    else -> {
                        val currentScope = scopeGenerator.generateScope(prevScope, LocalDate.now())
                        state.copy(
                            scopeType = prevScope,
                            taskList = taskRepo.observeTasksForScope(pagingConfig, currentScope)
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