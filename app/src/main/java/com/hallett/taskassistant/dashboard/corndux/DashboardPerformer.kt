package com.hallett.taskassistant.dashboard.corndux

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.ClickTaskInList
import com.hallett.taskassistant.corndux.LoadLargerScope
import com.hallett.taskassistant.corndux.LoadSmallerScope
import com.hallett.taskassistant.corndux.UpdateExpandedTask
import com.hallett.taskassistant.corndux.UpdateTypedTaskList
import com.hallett.taskassistant.corndux.utils.TaskListTransformer
import java.time.LocalDate

class DashboardPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val transformer: TaskListTransformer,
) : StatefulPerformer<DashboardState> {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: DashboardState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> {
                val currentScope = scopeCalculator.generateScope(
                    state.scopeType,
                    LocalDate.now()
                )

                dispatchCommit(
                    UpdateTypedTaskList(
                        scopeType = state.scopeType,
                        taskList = transformer.transform(
                            tasks = taskRepo.observeTasksForScope(pagingConfig, currentScope),
                            includeHeaders = false
                        )
                    )
                )
            }
            is LoadSmallerScope -> {
                val prevScopeType = state.scopeType.previous()
                if (prevScopeType != state.scopeType) {
                    val prevScope = scopeCalculator.generateScope(prevScopeType, LocalDate.now())

                    dispatchCommit(
                        UpdateTypedTaskList(
                            scopeType = prevScopeType,
                            taskList = transformer.transform(
                                tasks = taskRepo.observeTasksForScope(pagingConfig, prevScope),
                                includeHeaders = false
                            )
                        )
                    )
                }
            }
            is LoadLargerScope -> {
                val nextScopeType = state.scopeType.next()
                if (nextScopeType != state.scopeType) {
                    val nextScope = scopeCalculator.generateScope(nextScopeType, LocalDate.now())

                    dispatchCommit(
                        UpdateTypedTaskList(
                            scopeType = nextScopeType,
                            taskList = transformer.transform(
                                tasks = taskRepo.observeTasksForScope(pagingConfig, nextScope),
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