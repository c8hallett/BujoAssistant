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
        when(sideEffect) {
            NavigateUp -> withContext(dispatchers.main){ navController.popBackStack() }
        }
    }
}