package com.hallett.taskassistant.corndux.performers

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.performers.actions.OverdueTaskAction
import com.hallett.taskassistant.corndux.performers.utils.TaskListTransformer
import com.hallett.taskassistant.corndux.reducers.UpdateOverdueExpandedTask
import com.hallett.taskassistant.corndux.reducers.UpdateOverdueTaskList
import java.time.LocalDate

class OverdueTaskPerformer(
    private val taskRepo: ITaskRepository,
    private val transformer: TaskListTransformer,
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
                dispatchCommit(
                    UpdateOverdueTaskList(
                        taskList = transformer.transform(
                            tasks = taskRepo.getOverdueTasks(pagingConfig, LocalDate.now()),
                            includeHeaders = false
                        )
                    )
                )
            }
            !is OverdueTaskAction -> {}
            is OverdueTaskAction.TaskClickedInList -> {
                val newTask = when(action.task){
                    state.components.overdueTask.currentlyExpandedTask -> null
                    else -> action.task
                }
                dispatchCommit(
                    UpdateOverdueExpandedTask(newTask)
                )
            }
            is AddRandomOverdueTask -> {
                taskRepo.randomTask(ScopeType.values().random(), overdue = true)
            }
        }
    }
}