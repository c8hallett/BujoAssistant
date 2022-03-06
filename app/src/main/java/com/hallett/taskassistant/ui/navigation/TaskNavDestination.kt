package com.hallett.taskassistant.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.hallett.taskassistant.R
import com.hallett.taskassistant.corndux.actions.BottomNavigationClicked
import com.hallett.taskassistant.corndux.actions.DashboardClicked
import com.hallett.taskassistant.corndux.actions.OverdueTasksClicked
import com.hallett.taskassistant.corndux.actions.TaskListClicked

sealed class TaskNavDestination(val route: String) {

    companion object {
        val startDestination: TaskNavDestination
            get() = TaskList
    }

    object CreateTask: TaskNavDestination("taskCreate")

    object TaskList: TaskNavDestination("taskList"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_task_list
        override val icon: ImageVector = Icons.Filled.List
        override val action = TaskListClicked(this)
    }

    object Dashboard: TaskNavDestination("dashboard"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_dashboard
        override val icon: ImageVector = Icons.Filled.Dashboard
        override val action: BottomNavigationClicked = DashboardClicked(this)
    }

    object OverdueTasks: TaskNavDestination("overdueList"), BottomNavigationScreen {
        override val labelResId: Int = R.string.navigation_label_overdue_tasks
        override val icon: ImageVector = Icons.Filled.ErrorOutline
        override val action = OverdueTasksClicked(this)
    }
}