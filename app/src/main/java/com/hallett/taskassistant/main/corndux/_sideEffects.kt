package com.hallett.taskassistant.main.corndux

import com.hallett.corndux.SideEffect
import com.hallett.taskassistant.main.TaskNavDestination

object NavigateUp : SideEffect
data class NavigateToRootDestination(val destination: TaskNavDestination) : SideEffect
data class NavigateSingleTop(val destination: TaskNavDestination) : SideEffect