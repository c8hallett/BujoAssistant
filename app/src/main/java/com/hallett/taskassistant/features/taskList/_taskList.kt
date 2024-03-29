package com.hallett.taskassistant.features.taskList

import WithStore
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import collectState
import com.hallett.taskassistant.features.genericTaskList.TaskList
import com.hallett.taskassistant.features.scopeSelection.ScopeSelection
import com.hallett.taskassistant.features.taskList.corndux.TaskListStore
import org.kodein.di.compose.rememberInstance


@Composable
fun OpenTaskList() {
    val taskListStore by rememberInstance<TaskListStore>()
    WithStore(taskListStore) {
        val state by taskListStore.collectState()
        val pagedTasks = state.taskList.collectAsLazyPagingItems()
        val isSelectActive = state.scopeSelectionInfo != null


        Column {
            val scopeSelectionHeight = when {
                isSelectActive -> Modifier.fillMaxHeight(0.38f)
                else -> Modifier.wrapContentHeight()
            }

            ScopeSelection(
                scope = state.scope,
                scopeSelectionInfo = state.scopeSelectionInfo,
                modifier = scopeSelectionHeight.padding(horizontal = 12.dp)
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
