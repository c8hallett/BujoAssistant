package com.hallett.taskassistant.mainNavigation.corndux

import androidx.navigation.NavController
import com.hallett.corndux.SideEffect
import com.hallett.corndux.SideEffectPerformer
import com.hallett.taskassistant.corndux.sideeffects.NavigateSingleTop
import com.hallett.taskassistant.corndux.sideeffects.NavigateToRootDestination
import com.hallett.taskassistant.corndux.sideeffects.NavigateUp

class NavigationSideEffectPerformer(private val navController: NavController): SideEffectPerformer {
    override fun performSideEffect(sideEffect: SideEffect) {
        when (sideEffect) {
            is NavigateUp -> navController.popBackStack()
            is NavigateToRootDestination -> navController.navigate(sideEffect.destination.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
            is NavigateSingleTop -> navController.navigate(sideEffect.destination.route) {
                launchSingleTop = true
            }
        }
    }
}