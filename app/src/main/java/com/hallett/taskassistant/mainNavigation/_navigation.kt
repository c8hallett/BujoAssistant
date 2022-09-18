package com.hallett.taskassistant.mainNavigation

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hallett.corndux.SideEffect
import com.hallett.logging.logI
import com.hallett.taskassistant.LocalStore
import com.hallett.taskassistant.corndux.FabClicked
import com.hallett.taskassistant.corndux.NavigateSingleTop
import com.hallett.taskassistant.corndux.NavigateToRootDestination
import com.hallett.taskassistant.corndux.NavigateUp
import com.hallett.taskassistant.features.createTasks.TaskCreation
import com.hallett.taskassistant.features.dashboard.TaskDashboard
import com.hallett.taskassistant.features.futureTasks.FutureTaskList
import com.hallett.taskassistant.features.overdueTasks.OverdueTasks
import com.hallett.taskassistant.features.taskList.OpenTaskList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.kodein.di.compose.rememberInstance


@FlowPreview
@ExperimentalMaterialApi
@Composable
fun MainNavHost(innerPadding: PaddingValues, navController: NavHostController) {
    logI("MainNavHost", "start destination = ${TaskNavDestination.startDestination}")
    NavHost(
        navController = navController,
        startDestination = TaskNavDestination.startDestination.route,
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
        composable(TaskNavDestination.FutureTaskList.route) {
            FutureTaskList()
        }
    }

    val store = LocalStore.current

    LaunchedEffect(key1 = store) {
        store.observeSideEffects().onEach { sideEffect: SideEffect ->
            when (sideEffect) {
                is NavigateUp -> navController.popBackStack()
                is NavigateToRootDestination -> navController.navigate(sideEffect.destination.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
                is NavigateSingleTop -> navController.navigate(sideEffect.destination.route) {
                    launchSingleTop = true
                }
            }
        }.collect()
    }
}

@Composable
fun TaskBottomAppBar() {
    val items = listOf(
        TaskNavDestination.Dashboard,
        TaskNavDestination.FutureTaskList,
        TaskNavDestination.OverdueTasks,
        TaskNavDestination.TaskList,
    )
    TaskBottomAppBarImpl(items = items)
}

@Composable
private fun TaskBottomAppBarImpl(items: List<BottomNavigationScreen>) {
    val store = LocalStore.current
    val navController: NavController by rememberInstance()

    BottomAppBar {
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
    val store = LocalStore.current
    FloatingActionButton(
        onClick = { store.dispatch(FabClicked(TaskNavDestination.CreateTask)) }
    ) {
        Icon(Icons.Default.Add, "new task")
    }
}