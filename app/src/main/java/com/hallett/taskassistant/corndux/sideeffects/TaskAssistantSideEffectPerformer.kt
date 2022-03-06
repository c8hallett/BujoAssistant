package com.hallett.taskassistant.corndux.sideeffects

import androidx.navigation.NavController
import com.hallett.corndux.SideEffect
import com.hallett.corndux.SideEffectPerformer
import com.hallett.domain.coroutines.DispatchersWrapper
import kotlinx.coroutines.withContext

class TaskAssistantSideEffectPerformer(
    private val navController: NavController,
    private val dispatchers: DispatchersWrapper
): SideEffectPerformer {
    override suspend fun performSideEffect(sideEffect: SideEffect) {
        withContext(dispatchers.main){
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