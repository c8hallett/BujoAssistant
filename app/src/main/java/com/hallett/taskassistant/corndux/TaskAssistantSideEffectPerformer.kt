package com.hallett.taskassistant.corndux

import androidx.navigation.NavController
import com.hallett.corndux.SideEffectPerformer
import com.hallett.logging.logI

class TaskAssistantSideEffectPerformer(): SideEffectPerformer<TaskAssistantSideEffect> {
    override fun performSideEffect(sideEffect: TaskAssistantSideEffect) {
        when(sideEffect) {
            NavigateUp -> logI("Should pop the back stack somehow")// navController.popBackStack()
        }
    }
}