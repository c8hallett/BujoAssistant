package com.hallett.taskassistant.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hallett.taskassistant.ui.composables.OpenTaskList
import com.hallett.taskassistant.ui.composables.OverdueTasks
import com.hallett.taskassistant.ui.composables.TaskCreation
import org.kodein.di.DI


@ExperimentalMaterialApi
@Composable
fun MainNavHost(innerPadding: PaddingValues, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TaskNavDestination.TaskList.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(
            TaskNavDestination.TaskEdit.route,
            arguments = TaskNavDestination.TaskEdit.args
        ) { _ ->
            TaskCreation()
        }
        composable(TaskNavDestination.TaskList.route) {
            OpenTaskList(
                navController = navController
            )
        }
        composable(TaskNavDestination.OverdueTasks.route) {
            OverdueTasks(
                navController = navController
            )
        }

        // TODO: add composable for overdue tasks
    }
}

@Composable
fun TaskBottomAppBar(navController: NavHostController) {
    val items = listOf(
        TaskNavDestination.TaskList,
        TaskNavDestination.OverdueTasks
    )
    TaskBottomAppBarImpl(navController = navController, items = items)
}
@Composable
private fun TaskBottomAppBarImpl(navController: NavHostController, items: List<BottomNavigationScreen>) {
    BottomAppBar(
        cutoutShape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))
    ) {
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(screen.labelResId)) },
                selected = navController.currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun TaskFloatingActionBar(navController: NavHostController) {
    FloatingActionButton(
        onClick = {
            navController.navigate(route = TaskNavDestination.TaskEdit.calculateRoute()) {
                cleanupBackstack(navController)
            }
        }
    ) {
        Icon(Icons.Default.Add, "new task")
    }
}

fun NavOptionsBuilder.cleanupBackstack(navController: NavController) {
    popUpTo(navController.graph.startDestinationId){ inclusive = false }
    launchSingleTop = true
}