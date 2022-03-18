package com.hallett.taskassistant.dashboard.corndux

import com.hallett.corndux.Actor
import com.hallett.taskassistant.corndux.performers.TaskActionsPerformer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val dashboardModule = DI.Module("dashboardModule") {
    bindSingleton {
        DashboardStore(
            actors = instance(),
            scope = instance()
        )
    }
    bindSingleton<List<Actor<out DashboardState>>> {
        listOf(
            DashboardPerformer(instance(), instance(), instance()),
            TaskActionsPerformer(instance(), instance()),
            DashboardReducer(),
        )
    }
}