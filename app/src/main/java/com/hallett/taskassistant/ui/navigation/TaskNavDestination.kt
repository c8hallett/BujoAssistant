package com.hallett.taskassistant.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.hallett.taskassistant.R
import com.hallett.taskassistant.domain.Task

sealed class TaskNavDestination(val route: String) {
    private companion object {
        const val ARG_TASK_ID = "taskId"
        const val PATH_TASK_EDIT = "taskEdit/"

        const val ROUTE_TASK_EDIT = "$PATH_TASK_EDIT{$ARG_TASK_ID}"
        const val ROUTE_TASK_LIST = "taskList"
        const val ROUTE_OVERDUE_LIST = "overdueList"
    }

    object TaskEdit: TaskNavDestination(ROUTE_TASK_EDIT) {
        fun calculateRoute(id: Long = Task.DEFAULT_VALUE.id) = "$PATH_TASK_EDIT/$id"
        val args = listOf(navArgument(ARG_TASK_ID) {
            type = NavType.LongType
            defaultValue = Task.DEFAULT_VALUE.id
        })

        fun getTaskId(backStack: NavBackStackEntry): Long {
            return backStack.arguments?.getLong(ARG_TASK_ID) ?: Task.DEFAULT_VALUE.id
        }
    }

    object TaskList: TaskNavDestination(ROUTE_TASK_LIST), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_task_list
        override val icon: ImageVector = Icons.Filled.List
    }

    object OverdueTasks: TaskNavDestination(ROUTE_OVERDUE_LIST), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_overdue_tasks
        override val icon: ImageVector = Icons.Filled.ErrorOutline
    }
}