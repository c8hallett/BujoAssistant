package com.hallett.taskassistant.futureTasks.corndux

import com.hallett.corndux.Actor
import com.hallett.taskassistant.corndux.middleware.LoggingMiddleware
import com.hallett.taskassistant.corndux.performers.TaskActionsPerformer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val futureModule = DI.Module("futureModule") {

    bindSingleton {
        FutureStore(
            actors = instance(),
            scope = instance()
        )
    }
    bindSingleton<List<Actor<out FutureState>>> {
        listOf(
            FuturePerformer(instance(), instance()),
            TaskActionsPerformer(instance(), instance()),
            FutureReducer(),
            LoggingMiddleware(),
        )
    }
}