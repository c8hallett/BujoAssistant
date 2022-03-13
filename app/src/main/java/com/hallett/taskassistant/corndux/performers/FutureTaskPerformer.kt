package com.hallett.taskassistant.corndux.performers

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.ExpandList
import com.hallett.taskassistant.corndux.performers.utils.TaskListTransformer
import com.hallett.taskassistant.corndux.reducers.UpdateCurrentlyExpandedList
import com.hallett.taskassistant.corndux.reducers.UpdateFutureTaskLists
import com.hallett.taskassistant.ui.model.TaskAction
import com.hallett.taskassistant.ui.model.TaskView
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FutureTaskPerformer(
    private val taskRepo: ITaskRepository,
    private val transformer: TaskListTransformer
): IPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when(action) {
            is Init -> {
                val unscheduledList = taskRepo.observeTasksForScope(pagingConfig, null, false)
                val scheduledList = taskRepo.observeFutureTasks(pagingConfig, LocalDate.now())
                dispatchCommit(
                    UpdateFutureTaskLists(
                        unscheduled = transformer.transform(
                            tasks = unscheduledList,
                            includeHeaders = false
                        ),
                        scheduled = transformer.transform(
                            tasks = scheduledList,
                            includeHeaders = true
                        )
                    )
                )
            }
            is ExpandList -> dispatchCommit(UpdateCurrentlyExpandedList(action.list))
        }
    }
}