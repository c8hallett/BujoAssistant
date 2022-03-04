package com.hallett.taskassistant.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.hallett.taskassistant.R
import com.hallett.domain.model.Task
import com.hallett.taskassistant.corndux.BottomNavigationClicked
import com.hallett.taskassistant.corndux.DashboardClicked
import com.hallett.taskassistant.corndux.OverdueTasksClicked
import com.hallett.taskassistant.corndux.TaskListClicked

sealed class TaskNavDestination(val route: String) {
    private companion object {
        const val ROUTE_TASK_CREATE = "taskCreate"
        const val ROUTE_TASK_LIST = "taskList"
        const val ROUTE_OVERDUE_LIST = "overdueList"
        const val ROUTE_DASHBOARD = "dashboard"
    }

    object CreateTask: TaskNavDestination(ROUTE_TASK_CREATE)

    object TaskList: TaskNavDestination(ROUTE_TASK_LIST), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_task_list
        override val icon: ImageVector = Icons.Filled.List
        override val action = TaskListClicked(this)
    }

    object Dashboard: TaskNavDestination(ROUTE_DASHBOARD), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_dashboard
        override val icon: ImageVector = Icons.Filled.Dashboard
        override val action: BottomNavigationClicked = DashboardClicked(this)
    }

    object OverdueTasks: TaskNavDestination(ROUTE_OVERDUE_LIST), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_overdue_tasks
        override val icon: ImageVector = Icons.Filled.ErrorOutline
        override val action = OverdueTasksClicked(this)
    }
}