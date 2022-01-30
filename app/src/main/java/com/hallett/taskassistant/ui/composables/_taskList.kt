package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hallett.taskassistant.domain.Task
import com.hallett.taskassistant.ui.theme.TaskListViewModel
import org.kodein.di.DI
import org.kodein.di.compose.withDI

@Composable
fun TaskList(viewModel: TaskListViewModel, di: DI, navController: NavController) = withDI(di) {
    val pagedTask = viewModel.observeTasksForCurrentScope().collectAsLazyPagingItems()
    LazyColumn{
        items(pagedTask) { task ->
            when(task){
                null -> Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp))
                else -> TaskItem(task = task, modifier = Modifier.clickable { navController.navigate("taskId/${task.id}") })
            }
        }

    }

}

@Composable
fun TaskItem(task: Task, modifier: Modifier) {
    Card(modifier = modifier) {
        Text(task.taskName, style = MaterialTheme.typography.h3)
    }
}