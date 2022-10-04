package com.hallett.taskassistant.features.taskList.corndux

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
import com.hallett.taskassistant.features.scopeSelection.ClickNewScope
import com.hallett.taskassistant.main.corndux.UpdateSelectedScope
import com.hallett.taskassistant.main.corndux.UpdateTaskList

class TaskListPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator,
    private val transformer: TaskListTransformer,
) : StatefulPerformer<TaskListState> {

    private val pagingConfig = PagingConfig(pageSize = 20)
    private val taskQueryBuilder = TaskQueryBuilder()

    override fun performAction(
        state: TaskListState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> {
                taskQueryBuilder.filterByScopeType(null)
                dispatchAction(
                    UpdateTaskList(
                        taskList = transformer.transform(
                            tasks = taskRepo.queryTasks(
                                pagingConfig,
                                taskQueryBuilder
                            ),
                            includeHeaders = false
                        )
                    )
                )
                dispatchAction(UpdateSelectedScope(null))
            }
            is UpdateSelectedScope -> {
                taskQueryBuilder.filterByScope(action.scope)
                dispatchAction(
                    UpdateTaskList(
                        taskList = transformer.transform(
                            tasks = taskRepo.queryTasks(
                                pagingConfig,
                                taskQueryBuilder
                            ),
                            includeHeaders = false
                        )
                    )
                )
            }
        }
    }
}