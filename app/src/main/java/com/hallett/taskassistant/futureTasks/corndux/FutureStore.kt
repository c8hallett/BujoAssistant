package com.hallett.taskassistant.futureTasks.corndux

import androidx.paging.PagingData
import com.hallett.corndux.Action
import com.hallett.corndux.Actor
import com.hallett.corndux.Commit
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.taskassistant.ui.model.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FutureStore(
    initialState: FutureState = FutureState(),
    actors: List<Actor<out FutureState>>,
    scope: CoroutineScope
) : Store<FutureState>(initialState, actors, scope)

data class FutureState(
    val currentlyExpandedTask: Task? = null,
    val currentList: Flow<PagingData<TaskView>> = flowOf(),
    val currentListType: ListType = ListType.UNSCHEDULED
) : IState

enum class ListType {
    SCHEDULED,
    UNSCHEDULED
}

data class ExpandList(val listType: ListType) : Action

data class UpdateExpandedList(
    val taskList: Flow<PagingData<TaskView>>,
    val listType: ListType
) : Commit