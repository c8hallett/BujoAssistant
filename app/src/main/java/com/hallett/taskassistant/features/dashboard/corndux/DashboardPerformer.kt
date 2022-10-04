package com.hallett.taskassistant.features.dashboard.corndux

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.database.room.TaskQueryBuilder
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.ui.genericTaskList.TaskListTransformer
import com.hallett.taskassistant.main.corndux.LoadLargerScope
import com.hallett.taskassistant.main.corndux.LoadSmallerScope
import com.hallett.taskassistant.main.corndux.UpdateTypedTaskList
import java.time.LocalDate

class DashboardPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val transformer: TaskListTransformer,
) : StatefulPerformer<DashboardState> {

    private val pagingConfig = PagingConfig(pageSize = 20)
    private val taskQueryBuilder = TaskQueryBuilder()

    override fun performAction(
        state: DashboardState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> {
                val currentScope = scopeCalculator.generateScope(
                    state.scopeType,
                    LocalDate.now()
                )
                taskQueryBuilder.filterByScope(currentScope)

                dispatchAction(
                    UpdateTypedTaskList(
                        scopeType = state.scopeType,
                        taskList = transformer.transform(
                            tasks = taskRepo.queryTasks(pagingConfig, taskQueryBuilder),
                            includeHeaders = false
                        )
                    )
                )
            }
            is LoadSmallerScope -> {
                val prevScopeType = state.scopeType.previous()
                if (prevScopeType != state.scopeType) {
                    val prevScope = scopeCalculator.generateScope(prevScopeType, LocalDate.now())
                    taskQueryBuilder.filterByScope(prevScope)

                    dispatchAction(
                        UpdateTypedTaskList(
                            scopeType = prevScopeType,
                            taskList = transformer.transform(
                                tasks = taskRepo.queryTasks(pagingConfig, taskQueryBuilder),
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
                    taskQueryBuilder.filterByScope(nextScope)

                    dispatchAction(
                        UpdateTypedTaskList(
                            scopeType = nextScopeType,
                            taskList = transformer.transform(
                                tasks = taskRepo.queryTasks(pagingConfig, taskQueryBuilder),
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