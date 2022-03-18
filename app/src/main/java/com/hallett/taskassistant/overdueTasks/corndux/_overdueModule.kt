package com.hallett.taskassistant.overdueTasks.corndux

import com.hallett.corndux.Actor
import com.hallett.taskassistant.corndux.performers.TaskActionsPerformer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance


val overdueModule = DI.Module("overdueModule") {
    bindSingleton {
        OverdueStore(
            actors = instance(),
            scope = instance()
        )
    }
    bindSingleton<List<Actor<out OverdueState>>> {
        listOf(
            OverduePerformer(instance(), instance()),
            TaskActionsPerformer(instance(), instance()),
            OverdueReducer(),
        )
    }
}