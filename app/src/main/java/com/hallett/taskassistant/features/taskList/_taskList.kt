package com.hallett.taskassistant.features.taskList

import WithStore
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import collectState
import com.hallett.taskassistant.features.scopeSelection.ScopeSelection
import com.hallett.taskassistant.features.taskList.corndux.TaskListStore
import com.hallett.taskassistant.main.corndux.UpdateSelectedScope
import com.hallett.taskassistant.ui.genericTaskList.TaskList
import com.hallett.taskassistant.ui.rescheduleDialog.RescheduleTaskDialog
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
                onScopeSelected = { taskListStore.dispatch(UpdateSelectedScope(it)) }
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

        state.currentlySchedulingTask?.let { rescheduleTask ->
            RescheduleTaskDialog(task = rescheduleTask)
        }
    }
}
