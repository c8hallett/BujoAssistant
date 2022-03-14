package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import com.hallett.taskassistant.corndux.performers.actions.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.performers.actions.OverdueTaskAction
import taskAssistantStore

@Composable
fun OverdueTasks() {
    val store by taskAssistantStore()
    val state by store.observeState { it.components.overdueTask }.collectAsState()
    val pagedTasks = state.taskList.collectAsLazyPagingItems()

    Column {
        Text("Overdue tasks", style = MaterialTheme.typography.h6)
        Button(onClick = { store.dispatch(AddRandomOverdueTask) }) {
            Text("Random Overdue Task")
        }

        if (pagedTasks.itemCount == 0) {
            Text("No overdue tasks!")
        } else {
            TaskList(
                pagedTasks = pagedTasks,
                isTaskExpanded = { state.currentlyExpandedTask == it },
                onTaskClickedAction = { OverdueTaskAction.TaskClickedInList(it) }
            )
        }
    }
}
