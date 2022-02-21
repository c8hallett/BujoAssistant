package com.hallett.taskassistant.ui.composables

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.kodein.di.DI
import org.kodein.di.compose.withDI

@Composable
fun OverdueTasks(di: DI, navController: NavController) = withDI(di) {
    Text("Overdue tasks", style = MaterialTheme.typography.h2)
}
