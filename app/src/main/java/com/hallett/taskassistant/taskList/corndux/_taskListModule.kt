package com.hallett.taskassistant.taskList.corndux

import com.hallett.corndux.Actor
import com.hallett.taskassistant.corndux.middleware.LoggingMiddleware
import com.hallett.taskassistant.corndux.performers.TaskActionsPerformer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val _taskListModule = DI.Module("taskListModule") {
    bindSingleton {
        TaskListStore(
            actors = instance(),
            scope = instance()
        )
    }
    bindSingleton<List<Actor<out TaskListState>>> {
        listOf(
            TaskListPerformer(instance(), instance(), instance(), instance()),
            TaskActionsPerformer(instance(), instance()),
            TaskListReducer(),
            LoggingMiddleware(),
        )
    }
}