package com.hallett.taskassistant.features.futureTasks.corndux

import androidx.paging.PagingData
import com.hallett.corndux.Action
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.taskassistant.ui.model.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FutureStore(
    futurePerformer: FuturePerformer,
    futureReducer: FutureReducer,
    scope: CoroutineScope
) : Store<FutureState>(
    initialState = FutureState(),
    actors = listOf(futurePerformer, futureReducer),
    scope = scope
)

data class FutureState(
    val expandedTask: Task? = null,
    val list: Flow<PagingData<TaskView>> = flowOf(),
    val listType: ListType = ListType.UNSCHEDULED,
    val search: String = ""
) : IState

enum class ListType {
    SCHEDULED,
    UNSCHEDULED
}

data class ExpandList(val listType: ListType) : Action

data class UpdateExpandedList(
    val taskList: Flow<PagingData<TaskView>>,
    val listType: ListType
) : Action