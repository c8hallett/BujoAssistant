package com.hallett.taskassistant.corndux

import com.hallett.corndux.ISideEffect

sealed interface TaskAssistantSideEffect: ISideEffect

object NavigateUp: TaskAssistantSideEffect