package com.hallett.taskassistant.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hallett.domain.model.Task
import com.hallett.domain.model.TaskStatus
import com.hallett.taskassistant.corndux.performers.actions.DeferTask
import com.hallett.taskassistant.corndux.performers.actions.DeleteTask
import com.hallett.taskassistant.corndux.performers.actions.TaskClickedInList
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
            modifier = scopeSelectionHeight.padding(horizontal = 12.dp)
        )

        if(pagedTasks.itemCount == 0) {
            Text("No tasks!")
        } else {
            TaskList(
                pagedTasks = pagedTasks,
                isTaskExpanded = { it == state.currentlyExpandedTask },
                onTaskClickedAction = { TaskClickedInList(it) }
            )
        }
    }
}
