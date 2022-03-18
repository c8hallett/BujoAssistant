package com.hallett.taskassistant.futureTasks.corndux

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.Performer
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.taskassistant.corndux.ClickTaskInList
import com.hallett.taskassistant.corndux.UpdateExpandedTask
import com.hallett.taskassistant.corndux.utils.TaskListTransformer
import java.time.LocalDate

class FuturePerformer(
    private val taskRepo: ITaskRepository,
    private val transformer: TaskListTransformer
) : Performer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> {
                val unscheduledList = taskRepo.observeTasksForScope(pagingConfig, null, false)
                dispatchCommit(
                    UpdateExpandedList(
                        taskList = transformer.transform(unscheduledList, false),
                        listType = ListType.UNSCHEDULED
                    )
                )
            }
            is ClickTaskInList -> dispatchCommit(UpdateExpandedTask(action.task))
            is ExpandList -> {
                val list = when (action.listType) {
                    ListType.SCHEDULED -> transformer.transform(
                        tasks = taskRepo.observeFutureTasks(pagingConfig, LocalDate.now()),
                        includeHeaders = true
                    )

                    ListType.UNSCHEDULED -> transformer.transform(
                        tasks = taskRepo.observeTasksForScope(pagingConfig, null, false),
                        includeHeaders = false
                    )
                }

                dispatchCommit(
                    UpdateExpandedList(
                        taskList = list,
                        listType = action.listType
                    )
                )
            }
        }
    }
}