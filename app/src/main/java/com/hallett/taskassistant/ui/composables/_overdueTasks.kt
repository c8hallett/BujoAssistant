package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import org.kodein.di.DI
import org.kodein.di.compose.withDI
import overviewTaskViewModel

@Composable
fun OverdueTasks(di: DI, navController: NavController) = withDI(di) {
    val viewModel = di.overviewTaskViewModel()
    val pagedTasks = viewModel.observeOverdueTasks().collectAsLazyPagingItems()

    Column {
        Text("Overdue tasks", style = MaterialTheme.typography.h6)
        TaskList(pagedTasks = pagedTasks) {}
    }
}
