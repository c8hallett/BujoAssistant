package com.hallett.taskassistant.overdueTasks.corndux

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.Performer
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.ClickTaskInList
import com.hallett.taskassistant.corndux.UpdateExpandedTask
import com.hallett.taskassistant.corndux.UpdateTaskList
import com.hallett.taskassistant.corndux.utils.TaskListTransformer
import java.time.LocalDate

class OverduePerformer(
    private val taskRepo: ITaskRepository,
    private val transformer: TaskListTransformer,
) : Performer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> dispatchCommit(
                UpdateTaskList(
                    taskList = transformer.transform(
                        tasks = taskRepo.getOverdueTasks(pagingConfig, LocalDate.now()),
                        includeHeaders = false
                    )
                )
            )
            is ClickTaskInList -> dispatchCommit(UpdateExpandedTask(action.task))
            is AddRandomOverdueTask -> {
                taskRepo.randomTask(ScopeType.values().random(), overdue = true)
            }
        }
    }
}