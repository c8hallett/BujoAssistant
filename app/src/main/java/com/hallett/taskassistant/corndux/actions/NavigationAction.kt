package com.hallett.taskassistant.corndux.actions

import com.hallett.corndux.Action
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

sealed class NavigationAction(val destination: TaskNavDestination): Action
class FabClicked(destination: TaskNavDestination): NavigationAction(destination)

sealed class BottomNavigationClicked(destination: TaskNavDestination): NavigationAction(destination)
class TaskListClicked(destination: TaskNavDestination): BottomNavigationClicked(destination)
class DashboardClicked(destination: TaskNavDestination): BottomNavigationClicked(destination)
class OverdueTasksClicked(destination: TaskNavDestination): BottomNavigationClicked(destination)
