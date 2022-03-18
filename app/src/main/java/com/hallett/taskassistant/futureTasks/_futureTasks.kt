package com.hallett.taskassistant.futureTasks

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
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.taskassistant.corndux.overrideStoreType
import com.hallett.taskassistant.futureTasks.corndux.ExpandList
import com.hallett.taskassistant.futureTasks.corndux.FutureState
import com.hallett.taskassistant.futureTasks.corndux.ListType
import com.hallett.taskassistant.ui.composables.TaskList
import com.hallett.taskassistant.ui.model.TaskView
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.subDI

@Composable
fun FutureTaskList() {
    subDI(
        diBuilder = { overrideStoreType<Store<FutureState>>() }
    ) {
        val store by rememberInstance<Store<FutureState>>()
        val state by store.observeState().collectAsState()
        val taskList = state.currentList.collectAsLazyPagingItems()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ExpandableTaskList(
                label = "Sometime",
                items = if (state.currentListType == ListType.UNSCHEDULED) taskList else null,
                expandedTask = state.currentlyExpandedTask
            ) {
                store.dispatch(ExpandList(ListType.UNSCHEDULED))
            }
            ExpandableTaskList(
                label = "Scheduled",
                items = if (state.currentListType == ListType.SCHEDULED) taskList else null,
                expandedTask = state.currentlyExpandedTask
            ) {
                store.dispatch(ExpandList(ListType.SCHEDULED))
            }
        }
    }
}


@Composable
fun ColumnScope.ExpandableTaskList(
    label: String,
    expandedTask: Task?,
    items: LazyPagingItems<TaskView>?,
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

        if (items == null) {
            Icon(
                Icons.Default.ExpandMore,
                "",
                tint = MaterialTheme.colors.onPrimary,
            )
        }
    }
    if (items != null) {
        TaskList(
            pagedTasks = items,
            isTaskExpanded = { expandedTask == it },
            modifier = Modifier.weight(1.0f),
        )
    }
}