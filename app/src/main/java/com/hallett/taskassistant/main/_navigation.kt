package com.hallett.taskassistant.main

import LocalStore
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import collectState
import com.hallett.corndux.SideEffect
import com.hallett.logging.logI
import com.hallett.taskassistant.features.createTasks.TaskCreation
import com.hallett.taskassistant.features.dashboard.TaskDashboard
import com.hallett.taskassistant.features.limboTasks.FutureTaskList
import com.hallett.taskassistant.features.overdueTasks.OverdueTasks
import com.hallett.taskassistant.features.taskList.OpenTaskList
import com.hallett.taskassistant.main.corndux.ClickFab
import com.hallett.taskassistant.main.corndux.GlobalStore
import com.hallett.taskassistant.main.corndux.NavigateSingleTop
import com.hallett.taskassistant.main.corndux.NavigateToNewDestination
import com.hallett.taskassistant.main.corndux.NavigateToRootDestination
import com.hallett.taskassistant.main.corndux.NavigateUp
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
            arguments = TaskNavDestination.CreateTask.navArguments
        ) {
            TaskCreation(it.arguments?.getLong(TaskNavDestination.ARG_TASK_ID) ?: 0L)
        }
        composable(
            TaskNavDestination.TaskList.route,
            arguments = TaskNavDestination.TaskList.navArguments
        ) {
            OpenTaskList()
        }
        composable(
            TaskNavDestination.Dashboard.route,
            arguments = TaskNavDestination.Dashboard.navArguments
        ) {
            TaskDashboard()
        }
        composable(
            TaskNavDestination.OverdueTasks.route,
            arguments = TaskNavDestination.OverdueTasks.navArguments
        ) {
            OverdueTasks()
        }
        composable(
            TaskNavDestination.LimboTaskList.route,
            arguments = TaskNavDestination.LimboTaskList.navArguments
        ) {
            FutureTaskList()
        }
    }

    val store = LocalStore.current

    LaunchedEffect(key1 = store) {
        store.observeSideEffects().onEach { sideEffect: SideEffect ->
            when (sideEffect) {
                is NavigateUp -> navController.popBackStack()
                is NavigateToRootDestination -> navController.navigate(sideEffect.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                is NavigateSingleTop -> navController.navigate(sideEffect.route) {
                    launchSingleTop = true
                }
            }
        }.collect()
    }

    DisposableEffect(key1 = navController) {
        val navigationObserver = NavController.OnDestinationChangedListener { _, destination, _ ->
            store.dispatch(NavigateToNewDestination(destination))
        }
        navController.addOnDestinationChangedListener(navigationObserver)
        onDispose {
            navController.removeOnDestinationChangedListener(navigationObserver)
        }
    }
}

@Composable
fun TaskBottomAppBar(navController: NavHostController) {
    val items = listOf(
        TaskNavDestination.Dashboard,
        TaskNavDestination.LimboTaskList,
        TaskNavDestination.OverdueTasks,
        TaskNavDestination.TaskList,
    )
    TaskBottomAppBarImpl(items = items, navController = navController)
}

@Composable
private fun TaskBottomAppBarImpl(
    navController: NavHostController,
    items: List<BottomNavigationScreen>
) {
    val store = LocalStore.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentHierarchy = backStackEntry?.destination?.hierarchy

    BottomAppBar {
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                selected = currentHierarchy?.any { it.route == screen.route } == true,
                onClick = { store.dispatch(screen.action) }
            )
        }
    }
}

@Composable
fun TaskFloatingActionBar() {
    val globalStore by rememberInstance<GlobalStore>()
    val globalState by globalStore.collectState()

    if (globalState.shouldShowFab) {
        FloatingActionButton(
            onClick = {
                val route = TaskNavDestination.CreateTask.createRoute()
                globalStore.dispatch(ClickFab(route))
            }
        ) {
            Icon(Icons.Default.Add, "new task")
        }
    }
}