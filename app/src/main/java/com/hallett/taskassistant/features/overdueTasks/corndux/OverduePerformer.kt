package com.hallett.taskassistant.features.overdueTasks.corndux

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatelessPerformer
import com.hallett.database.ITaskRepository
import com.hallett.database.TaskSort
import com.hallett.database.room.TaskQueryBuilder
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.features.genericTaskList.TaskListTransformer
import com.hallett.taskassistant.main.corndux.AddRandomOverdueTask
import com.hallett.taskassistant.main.corndux.UpdateTaskList
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OverduePerformer(
    private val taskRepo: ITaskRepository,
    private val transformer: TaskListTransformer,
    private val workScope: CoroutineScope
) : StatelessPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)
    private val taskQueryBuilder = TaskQueryBuilder().apply {
        filterByStatuses(listOf(TaskStatus.COMPLETE), included = false)
        filterByDate(LocalDate.now(), false)
        sortBy(TaskSort.ScopeEnd(false))
    }

    override fun performAction(
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> {
                dispatchAction(
                    UpdateTaskList(
                        taskList = transformer.transform(
                            tasks = taskRepo.queryTasks(pagingConfig, taskQueryBuilder),
                            includeHeaders = true
                        )
                    )
                )
            }
            is AddRandomOverdueTask -> withRepo {
                randomTask(ScopeType.values().random(), overdue = true)
            }
        }
    }

    private inline fun withRepo(crossinline operation: suspend ITaskRepository.() -> Unit) =
        workScope.launch {
            taskRepo.operation()
        }
}