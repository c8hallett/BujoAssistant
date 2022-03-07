package com.hallett.taskassistant.corndux.actions

import com.hallett.corndux.Action
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

sealed class NavigationAction(val destination: TaskNavDestination): Action
class FabClicked(destination: TaskNavDestination): NavigationAction(destination)
class BottomNavigationClicked(destination: TaskNavDestination): NavigationAction(destination)
