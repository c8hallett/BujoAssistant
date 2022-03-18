package com.hallett.taskassistant.createTasks.corndux

import com.hallett.corndux.Actor
import com.hallett.taskassistant.mainNavigation.corndux.NavigationSideEffectPerformer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val createTaskModule = DI.Module("createTaskModule") {
    bindSingleton {
        CreateTaskStore(
            actors = instance(),
            scope = instance()
        )
    }
    bindSingleton<List<Actor<out CreateTaskState>>> {
        listOf(
            CreateTaskPerformer(instance(), instance()),
            CreateTaskReducer(),
            NavigationSideEffectPerformer(instance())
        )
    }
}