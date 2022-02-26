package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hallett.domain.model.Task
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import com.hallett.taskassistant.ui.navigation.cleanupBackstack
import org.kodein.di.compose.localDI
import taskListViewModel

@Composable
fun OpenTaskList() {
    val taskEditViewModel = localDI().taskListViewModel()
    val pagedTasks = taskEditViewModel.observeTasksForCurrentScope().collectAsLazyPagingItems()
    val scope by taskEditViewModel.observerCurrentScope().collectAsState(initial = null)
    val (isSelectActive, setSelectActive) = remember { mutableStateOf(false) }

    Column {

        val scopeSelectionHeight = when {
            isSelectActive -> Modifier.fillMaxHeight(0.38f)
            else -> Modifier.wrapContentHeight()
        }

//        ScopeSelection(
//            di = di,
//            scope = scope,
//            isSelectActive = isSelectActive,
//            onScopeSelected = taskEditViewModel::setCurrentScope,
//            setSelectActive = setSelectActive,
//            modifier = scopeSelectionHeight.padding(horizontal = 12.dp)
//        )

        TaskList(pagedTasks = pagedTasks) { task ->
        }
    }
}

@Composable
fun TaskList(pagedTasks: LazyPagingItems<Task>, onTaskClicked: (Task) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.SpaceBetween) {
        items(pagedTasks) { task ->
            when (task) {
                null -> Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                )
                else -> TaskItem(
                    task = task,
                    modifier = Modifier.clickable { onTaskClicked(task) })
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, modifier: Modifier) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)) {
        Text(task.taskName, style = MaterialTheme.typography.h3, modifier = Modifier.padding(12.dp))
    }
}