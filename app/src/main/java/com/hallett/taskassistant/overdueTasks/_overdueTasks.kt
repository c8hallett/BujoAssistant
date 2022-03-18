package com.hallett.taskassistant.overdueTasks

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import com.hallett.corndux.Store
import com.hallett.taskassistant.corndux.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.overrideStoreType
import com.hallett.taskassistant.overdueTasks.corndux.OverdueState
import com.hallett.taskassistant.ui.composables.TaskList
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.subDI

@Composable
fun OverdueTasks() {
    subDI(
        diBuilder = { overrideStoreType<Store<OverdueState>>() },
    ) {
        val store by rememberInstance<Store<OverdueState>>()
        val state by store.observeState().collectAsState()
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
                )
            }
        }
    }
}
