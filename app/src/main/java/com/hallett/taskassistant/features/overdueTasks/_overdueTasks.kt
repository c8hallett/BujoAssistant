package com.hallett.taskassistant.features.overdueTasks

import WithStore
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import com.hallett.taskassistant.corndux.AddRandomOverdueTask
import com.hallett.taskassistant.features.genericTaskList.TaskList
import com.hallett.taskassistant.features.overdueTasks.corndux.OverdueStore
import org.kodein.di.compose.rememberInstance

@Composable
fun OverdueTasks() {
    val overdueStore by rememberInstance<OverdueStore>()
    WithStore(overdueStore) {
        val state by overdueStore.observeState().collectAsState()
        val pagedTasks = state.taskList.collectAsLazyPagingItems()

        Column {
            Text("Overdue tasks", style = MaterialTheme.typography.h6)
            Button(onClick = { overdueStore.dispatch(AddRandomOverdueTask) }) {
                Text("Random Overdue Task")
            }

            if (pagedTasks.itemCount == 0) {
                Text("No overdue tasks!")
            } else {
                TaskList(
                    pagedTasks = pagedTasks,
                    isTaskExpanded = { state.currentlyExpandedTask == it },
                )
            }
        }
    }
}
