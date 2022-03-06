package com.hallett.taskassistant.corndux.sideeffects

import com.hallett.corndux.SideEffect
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

object NavigateUp: SideEffect
data class NavigateToRootDestination(val destination: TaskNavDestination): SideEffect
data class NavigateSingleTop(val destination: TaskNavDestination): SideEffect