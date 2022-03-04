package com.hallett.taskassistant.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hallett.taskassistant.corndux.FabClicked
import com.hallett.taskassistant.taskdashboard.TaskDashboard
import com.hallett.taskassistant.ui.composables.OpenTaskList
import com.hallett.taskassistant.ui.composables.OverdueTasks
import com.hallett.taskassistant.ui.composables.TaskCreation
import org.kodein.di.compose.rememberInstance
import taskAssistantStore


@ExperimentalMaterialApi
@Composable
fun MainNavHost(innerPadding: PaddingValues, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TaskNavDestination.TaskList.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(
            TaskNavDestination.CreateTask.route,
        ) {
            TaskCreation()
        }
        composable(TaskNavDestination.TaskList.route) {
            OpenTaskList()
        }
        composable(TaskNavDestination.Dashboard.route) {
            TaskDashboard()
        }
        composable(TaskNavDestination.OverdueTasks.route) {
            OverdueTasks()
        }
    }
}

@Composable
fun TaskBottomAppBar() {
    val items = listOf(
        TaskNavDestination.TaskList,
        TaskNavDestination.Dashboard,
        TaskNavDestination.OverdueTasks
    )
    TaskBottomAppBarImpl(items = items)
}
@Composable
private fun TaskBottomAppBarImpl(items: List<BottomNavigationScreen>) {
    val store by taskAssistantStore()
    val navController: NavController by rememberInstance()

    BottomAppBar() {
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(screen.labelResId)) },
                selected = navController.currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = { store.dispatch(screen.action) }
            )
        }
    }
}

@Composable
fun TaskFloatingActionBar() {
    val store by taskAssistantStore()
    FloatingActionButton(
        onClick = { store.dispatch(FabClicked(TaskNavDestination.CreateTask)) }
    ) {
        Icon(Icons.Default.Add, "new task")
    }
}