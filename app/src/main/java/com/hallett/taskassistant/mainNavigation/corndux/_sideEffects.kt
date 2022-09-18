package com.hallett.taskassistant.mainNavigation.corndux

import com.hallett.corndux.SideEffect
import com.hallett.taskassistant.mainNavigation.TaskNavDestination

object NavigateUp : SideEffect
data class NavigateToRootDestination(val destination: TaskNavDestination) : SideEffect
data class NavigateSingleTop(val destination: TaskNavDestination) : SideEffect