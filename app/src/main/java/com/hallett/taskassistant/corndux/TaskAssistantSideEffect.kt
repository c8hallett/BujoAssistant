package com.hallett.taskassistant.corndux

import com.hallett.corndux.ISideEffect
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

sealed interface TaskAssistantSideEffect: ISideEffect

object NavigateUp: TaskAssistantSideEffect
data class NavigateToRootDestination(val destination: TaskNavDestination): TaskAssistantSideEffect
data class NavigateSingleTop(val destination: TaskNavDestination): TaskAssistantSideEffect