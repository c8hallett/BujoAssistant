package com.hallett.taskassistant.futureTasks.corndux

import com.hallett.corndux.Actor
import com.hallett.taskassistant.corndux.performers.TaskActionsPerformer
import com.hallett.taskassistant.dashboard.corndux.DashboardPerformer
import com.hallett.taskassistant.dashboard.corndux.DashboardReducer
import com.hallett.taskassistant.dashboard.corndux.DashboardState
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
        )
    }
}