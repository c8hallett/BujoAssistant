package com.hallett.taskassistant.corndux

import androidx.navigation.NavController
import com.hallett.corndux.SideEffectPerformer
import com.hallett.domain.coroutines.DispatchersWrapper
import kotlinx.coroutines.withContext

class TaskAssistantSideEffectPerformer(
    private val navController: NavController,
    private val dispatchers: DispatchersWrapper
): SideEffectPerformer<TaskAssistantSideEffect> {
    override suspend fun performSideEffect(sideEffect: TaskAssistantSideEffect) {
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
            }
        }
    }
}