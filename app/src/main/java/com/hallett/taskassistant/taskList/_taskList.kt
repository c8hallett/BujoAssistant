package com.hallett.taskassistant.taskList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.hallett.corndux.Store
import com.hallett.taskassistant.corndux.overrideStoreType
import com.hallett.taskassistant.taskList.corndux.TaskListState
import com.hallett.taskassistant.ui.composables.ScopeSelection
import com.hallett.taskassistant.ui.composables.TaskList
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.subDI


@Composable
fun OpenTaskList() {
    subDI(
        diBuilder = { overrideStoreType<Store<TaskListState>>() }
    ) {
        val store by rememberInstance<Store<TaskListState>>()
        val state by store.observeState().collectAsState()
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