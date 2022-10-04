package com.hallett.taskassistant.features.taskList

import WithStore
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import collectState
import com.hallett.taskassistant.ui.genericTaskList.TaskList
import com.hallett.taskassistant.features.scopeSelection.ScopeSelection
import com.hallett.taskassistant.features.taskList.corndux.TaskListStore
import org.kodein.di.compose.rememberInstance


@Composable
fun OpenTaskList() {
    val taskListStore by rememberInstance<TaskListStore>()
    WithStore(taskListStore) {
        val state by taskListStore.collectState()
        val pagedTasks = state.taskList.collectAsLazyPagingItems()

        Column {
            ScopeSelection(
                scope = state.scope,
            )

            if (pagedTasks.itemCount == 0) {
                Text("No tasks!")
            } else {
                TaskList(
                    pagedTasks = pagedTasks,
                    isTaskExpanded = { it == state.currentlyExpandedTask },
                )
            }
        }
    }
}
