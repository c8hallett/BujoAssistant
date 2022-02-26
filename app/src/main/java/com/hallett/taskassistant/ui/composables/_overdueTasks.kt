package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import org.kodein.di.compose.localDI
import overviewTaskViewModel

@Composable
fun OverdueTasks(navController: NavController) {
    val viewModel = localDI().overviewTaskViewModel()
    val pagedTasks = viewModel.observeOverdueTasks().collectAsLazyPagingItems()

    Column {
        Text("Overdue tasks", style = MaterialTheme.typography.h6)
        Button(onClick = {viewModel.addRandomOverdueTask()}){
            Text("Random Overdue Task")
        }
        TaskList(pagedTasks = pagedTasks) {}
    }
}
