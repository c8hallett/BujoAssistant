package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.hallett.corndux.Action
import com.hallett.corndux.Event
import com.hallett.domain.model.Task
import com.hallett.taskassistant.corndux.FutureTaskListState.ExpandedList
import com.hallett.taskassistant.corndux.IInterpreter
import com.hallett.taskassistant.corndux.IStore
import com.hallett.taskassistant.corndux.interpreters.TaskInListClicked
import com.hallett.taskassistant.corndux.performers.actions.ExpandList
import com.hallett.taskassistant.corndux.performers.actions.FutureTaskAction
import com.hallett.taskassistant.ui.model.TaskView
import org.kodein.di.bindSingleton
import org.kodein.di.compose.subDI
import org.kodein.di.instance
import taskAssistantStore

class FutureTaskInterpeter(store: IStore): IInterpreter(store) {
    override fun mapEvent(event: Event): Action? = when (event){
        is TaskInListClicked -> FutureTaskAction.ClickTaskInList(event.task)
        else -> null
    }
}

@Composable
fun FutureTaskList() {
    subDI(diBuilder = {
        bindSingleton { FutureTaskInterpeter(instance()) }
    }) {
        val store by taskAssistantStore()
        val state by store.observeState { it.components.futureTasks }.collectAsState()
        val unscheduledTasks = state.unscheduledList.collectAsLazyPagingItems()
        val scheduledTasks = state.scheduledList.collectAsLazyPagingItems()


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ExpandableTaskList(
                label = "Sometime",
                isExpanded = state.expandedList == ExpandedList.UNSCHEDULED,
                items = unscheduledTasks,
                expandedTask = state.currentlyExpandedTask
            ) {
                store.dispatch(ExpandList(ExpandedList.UNSCHEDULED))
            }
            ExpandableTaskList(
                label = "Scheduled",
                isExpanded = state.expandedList == ExpandedList.SCHEDULED,
                items = scheduledTasks,
                expandedTask = state.currentlyExpandedTask
            ) {
                store.dispatch(ExpandList(ExpandedList.SCHEDULED))
            }
        }
    }
}


@Composable
fun ColumnScope.ExpandableTaskList(
    label: String,
    isExpanded: Boolean,
    expandedTask: Task?,
    items: LazyPagingItems<TaskView>,
    onHeaderClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primaryVariant)
            .padding(12.dp)
            .clickable { onHeaderClicked() },
    ) {
        Text(
            label,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h3,
            modifier = Modifier.padding(12.dp)
        )

        if (!isExpanded) {
            Icon(
                Icons.Default.ExpandMore,
                "",
                tint = MaterialTheme.colors.onPrimary,
            )
        }
    }
    if (isExpanded) {
        TaskList(
            pagedTasks = items,
            isTaskExpanded = { expandedTask == it },
            modifier = Modifier.weight(1.0f),
        )
    }
}