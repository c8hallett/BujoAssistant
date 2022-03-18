package com.hallett.taskassistant.mainNavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import com.hallett.domain.model.Task
import com.hallett.taskassistant.R
import com.hallett.taskassistant.corndux.performers.actions.BottomNavigationClicked

sealed class TaskNavDestination(val route: String) {

    companion object {
        val startDestination: TaskNavDestination
            get() = Dashboard
        fun fromRoute(route: String?): TaskNavDestination = when(route) {
            CreateTask.route -> CreateTask
            TaskList.route -> TaskList
            Dashboard.route -> Dashboard
            OverdueTasks.route -> OverdueTasks
            FutureTaskList.route -> FutureTaskList
            else -> startDestination
        }
    }

    object CreateTask : TaskNavDestination("taskCreate")

    object TaskList : TaskNavDestination("taskList"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_task_list
        override val icon: ImageVector = Icons.Filled.List
        override val action = BottomNavigationClicked(this)
    }

    object Dashboard : TaskNavDestination("dashboard"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_dashboard
        override val icon: ImageVector = Icons.Filled.Dashboard
        override val action: BottomNavigationClicked = BottomNavigationClicked(this)
    }

    object OverdueTasks : TaskNavDestination("overdueList"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_overdue_tasks
        override val icon: ImageVector = Icons.Filled.ErrorOutline
        override val action = BottomNavigationClicked(this)
    }

    object FutureTaskList : TaskNavDestination("futureTasks"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_future_tasks
        override val icon: ImageVector = Icons.Filled.Schedule
        override val action: BottomNavigationClicked = BottomNavigationClicked(this)
    }

}