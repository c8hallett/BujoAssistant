package com.hallett.taskassistant.corndux

import com.hallett.corndux.Actor
import com.hallett.corndux.Interpreter
import com.hallett.corndux.Middleware
import com.hallett.corndux.Performer
import com.hallett.corndux.Reducer
import com.hallett.corndux.Store
import kotlinx.coroutines.CoroutineScope

class TaskAssistantStore(
    initialState: TaskAssistantState,
    actors: List<Actor<TaskAssistantState>>,
    scope: CoroutineScope,
) : IStore(initialState, actors, scope)

typealias IStore = Store<TaskAssistantState>
typealias IReducer = Reducer<TaskAssistantState>
typealias IMiddleware = Middleware<TaskAssistantState>
typealias IPerformer = Performer<TaskAssistantState>
typealias IInterpreter = Interpreter<TaskAssistantState>