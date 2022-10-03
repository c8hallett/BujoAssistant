package com.hallett.taskassistant.features.limboTasks.corndux

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.database.TaskSort
import com.hallett.database.room.TaskQueryBuilder
import com.hallett.domain.model.TaskStatus
import com.hallett.taskassistant.features.genericTaskList.TaskListTransformer
import com.hallett.taskassistant.features.limboTasks.SearchUpdated
import com.hallett.taskassistant.main.corndux.UpdateTaskList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LimboPerformer(
    private val taskRepo: ITaskRepository,
    private val transformer: TaskListTransformer,
    private val scope: CoroutineScope,
) : StatefulPerformer<LimboState> {

    private val pagingConfig = PagingConfig(pageSize = 20)
    private val taskQueryBuilder = TaskQueryBuilder().apply {
        filterByScopeType(null)
        sortBy(TaskSort.Updated(true))
        filterByStatuses(listOf(TaskStatus.COMPLETE), included = false)
    }
    private var debounceJob: Job? = null

    override fun performAction(
        state: LimboState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> dispatchAction(fetchList(state.search))
            is SearchUpdated -> {
                debounceJob?.cancel()
                debounceJob = scope.launch {
                    delay(300L)
                    dispatchAction(fetchList(action.newSearch))
                }
            }
        }
    }

    private fun fetchList(search: String): Action {
        taskQueryBuilder.filterByTaskName(search)
        val list = transformer.transform(
            tasks = taskRepo.queryTasks(
                pagingConfig,
                taskQueryBuilder
            ),
            includeHeaders = false
        )
        return UpdateTaskList(taskList = list)
    }
}