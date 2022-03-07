package com.hallett.taskassistant.corndux.sideeffects

import androidx.navigation.NavController
import com.hallett.corndux.SideEffect
import com.hallett.corndux.SideEffectPerformer
import com.hallett.domain.coroutines.DispatchersWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NavigationSideEffectPerformer(
    private val navController: NavController,
    private val dispatchers: DispatchersWrapper,
    private val scope: CoroutineScope
): SideEffectPerformer {
    override fun performSideEffect(sideEffect: SideEffect) {
        scope.launch(dispatchers.main){
            when(sideEffect) {
                is NavigateUp -> navController.popBackStack()
                is NavigateToRootDestination -> navController.navigate(sideEffect.destination.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
                is NavigateSingleTop -> navController.navigate(sideEffect.destination.route) {
                    launchSingleTop = true
                }
                else -> {}
            }
        }
    }
}