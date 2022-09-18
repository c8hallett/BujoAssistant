package com.hallett.taskassistant.features.futureTasks.corndux

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.database.ITaskRepository
import com.hallett.taskassistant.features.futureTasks.SearchUpdated
import com.hallett.taskassistant.features.genericTaskList.TaskListTransformer
import java.time.LocalDate

class FuturePerformer(
    private val taskRepo: ITaskRepository,
    private val transformer: TaskListTransformer
) : StatefulPerformer<FutureState> {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override fun performAction(
        state: FutureState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when (action) {
            is Init -> dispatchAction(updateExpandedList(ListType.UNSCHEDULED, state.search))
            is ExpandList -> dispatchAction(updateExpandedList(action.listType, state.search))
            is SearchUpdated -> dispatchAction(updateExpandedList(state.listType, action.newSearch))
        }
    }

    private fun updateExpandedList(listType: ListType, search: String): Action {
        val list = when (listType) {
            ListType.SCHEDULED -> transformer.transform(
                tasks = taskRepo.observeFutureTasks(
                    pagingConfig,
                    LocalDate.now(),
                    search.nullIfBlank()
                ),
                includeHeaders = true
            )

            ListType.UNSCHEDULED -> transformer.transform(
                tasks = taskRepo.observeTasksForScope(
                    pagingConfig,
                    null,
                    search.nullIfBlank(),
                    false
                ),
                includeHeaders = false
            )
        }

        return UpdateExpandedList(
            taskList = list,
            listType = listType
        )
    }

    private fun String.nullIfBlank(): String? = when {
        this.isBlank() -> null
        else -> this
    }
}