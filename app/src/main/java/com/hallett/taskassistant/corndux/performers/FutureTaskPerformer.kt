package com.hallett.taskassistant.corndux.performers

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.ExpandList
import com.hallett.taskassistant.corndux.performers.actions.FutureTaskAction
import com.hallett.taskassistant.corndux.performers.utils.TaskListTransformer
import com.hallett.taskassistant.corndux.reducers.UpdateCurrentlyExpandedList
import com.hallett.taskassistant.corndux.reducers.UpdateFutureExpandedTask
import com.hallett.taskassistant.corndux.reducers.UpdateFutureTaskLists
import java.time.LocalDate

class FutureTaskPerformer(
    private val taskRepo: ITaskRepository,
    private val transformer: TaskListTransformer
) : IPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when (action) {
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
            !is FutureTaskAction -> {}
            is FutureTaskAction.ClickTaskInList -> {
                val newTask = when(action.task){
                    state.components.futureTasks.currentlyExpandedTask -> null
                    else -> action.task
                }
                dispatchCommit( UpdateFutureExpandedTask(newTask) )
            }
            is ExpandList -> dispatchCommit(UpdateCurrentlyExpandedList(action.list))
        }
    }
}