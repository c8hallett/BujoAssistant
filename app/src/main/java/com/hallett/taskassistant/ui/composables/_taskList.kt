package com.hallett.taskassistant.ui.composables

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
import com.hallett.taskassistant.corndux.performers.actions.TaskListAction
import taskAssistantStore

@Composable
fun OpenTaskList() {
    val store by taskAssistantStore()
    val state by store.observeState { it.components.taskList }.collectAsState()
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
            enterScopeSelectionAction = TaskListAction.EnterScopeSelection,
            cancelScopeSelectionAction = TaskListAction.CancelScopeSelection,
            selectScopeAction = { TaskListAction.SelectNewScope(it) },
            selectScopeTypeAction = { TaskListAction.SelectNewScopeType(it) },
            modifier = scopeSelectionHeight.padding(horizontal = 12.dp)
        )

        if (pagedTasks.itemCount == 0) {
            Text("No tasks!")
        } else {
            TaskList(
                pagedTasks = pagedTasks,
                isTaskExpanded = { it == state.currentlyExpandedTask },
                onTaskClickedAction = { TaskListAction.TaskClickedInList(it) }
            )
        }
    }
}
