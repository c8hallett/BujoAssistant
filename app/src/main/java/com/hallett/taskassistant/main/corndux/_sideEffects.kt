package com.hallett.taskassistant.main.corndux

import com.hallett.corndux.SideEffect

object NavigateUp : SideEffect
data class NavigateToRootDestination(val route: String) : SideEffect
data class NavigateSingleTop(val route: String) : SideEffect