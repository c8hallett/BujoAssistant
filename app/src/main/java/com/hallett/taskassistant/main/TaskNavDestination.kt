package com.hallett.taskassistant.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavAction
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.hallett.taskassistant.R
import com.hallett.taskassistant.main.corndux.ClickBottomNavigation

sealed class TaskNavDestination(val route: String) {
    open val navArguments: List<NamedNavArgument> = listOf()

    companion object {
        val startDestination: TaskNavDestination
            get() = Dashboard

        const val ARG_TASK_ID = "taskId"
    }

    object CreateTask : TaskNavDestination("taskCreate?taskId={$ARG_TASK_ID}"){
        override val navArguments: List<NamedNavArgument> = listOf(
            navArgument(ARG_TASK_ID){
                type = NavType.LongType
                defaultValue = (0L)
            }
        )

        fun createRoute(taskId: Long = 0L): String = "taskCreate?taskId=$taskId"
    }

    object TaskList : TaskNavDestination("taskList"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_task_list
        override val icon: ImageVector = Icons.Filled.List
        override val action = ClickBottomNavigation(route)
    }

    object Dashboard : TaskNavDestination("dashboard"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_dashboard
        override val icon: ImageVector = Icons.Filled.Dashboard
        override val action = ClickBottomNavigation(route)
    }

    object OverdueTasks : TaskNavDestination("overdueList"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_overdue_tasks
        override val icon: ImageVector = Icons.Filled.ErrorOutline
        override val action = ClickBottomNavigation(route)
    }

    object FutureTaskList : TaskNavDestination("futureTasks"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_future_tasks
        override val icon: ImageVector = Icons.Filled.Schedule
        override val action = ClickBottomNavigation(route)
    }

}