package com.hallett.taskassistant.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.paging.compose.items
import com.hallett.domain.model.Task
import com.hallett.taskassistant.corndux.FutureTaskListState.ExpandedList
import com.hallett.taskassistant.corndux.performers.actions.ExpandList
import com.hallett.taskassistant.corndux.performers.actions.TaskClickedInList
import com.hallett.taskassistant.ui.model.TaskView
import taskAssistantStore


@Composable
fun FutureTaskList() {
    val store by taskAssistantStore()
    val state by store.observeState { it.components.futureTasks }.collectAsState()
    val unscheduledTasks = state.unscheduledList.collectAsLazyPagingItems()
    val scheduledTasks = state.scheduledList.collectAsLazyPagingItems()


    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        ExpandableTaskList(
            label = "Sometime",
            isExpanded = state.expandedList == ExpandedList.UNSCHEDULED,
            items = unscheduledTasks) {
            store.dispatch(ExpandList(ExpandedList.UNSCHEDULED))
        }
        ExpandableTaskList(
            label = "Scheduled",
            isExpanded = state.expandedList == ExpandedList.SCHEDULED,
            items = scheduledTasks) {
            store.dispatch(ExpandList(ExpandedList.SCHEDULED))
        }
    }
}


@Composable
fun ColumnScope.ExpandableTaskList(
    label: String,
    isExpanded: Boolean,
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
    ){
        Text(
            label,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h3,
            modifier = Modifier.padding(12.dp)
        )

        if(!isExpanded) {
            Icon(
                Icons.Default.ExpandMore,
                "",
                tint = MaterialTheme.colors.onPrimary,
            )
        }
    }
    if(isExpanded) {
        // TODO: noItems composable funcion?
        // tODO: onExpanded composable function
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1.0f)
                .padding(vertical = 12.dp, horizontal = 12.dp)
        ) {
            items(items) { item ->
                when (item) {
                    is TaskView.TaskHolder -> TaskItem(
                        task = item.task,
                        expandedOptions = null,
                    ) {
                    }
                    is TaskView.HeaderHolder -> Text(
                        item.text,
                        style = MaterialTheme.typography.h6
                    )
                    else -> {}
                }
            }
        }
    }
}