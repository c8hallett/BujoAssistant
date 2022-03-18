package com.hallett.taskassistant.corndux

import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.taskassistant.corndux.middleware.LoggingMiddleware
import com.hallett.taskassistant.corndux.performers.TaskActionsPerformer
import com.hallett.taskassistant.createTasks.corndux.CreateTaskPerformer
import com.hallett.taskassistant.createTasks.corndux.CreateTaskReducer
import com.hallett.taskassistant.createTasks.corndux.CreateTaskStore
import com.hallett.taskassistant.dashboard.corndux.DashboardPerformer
import com.hallett.taskassistant.dashboard.corndux.DashboardReducer
import com.hallett.taskassistant.dashboard.corndux.DashboardStore
import com.hallett.taskassistant.futureTasks.corndux.FuturePerformer
import com.hallett.taskassistant.futureTasks.corndux.FutureReducer
import com.hallett.taskassistant.futureTasks.corndux.FutureStore
import com.hallett.taskassistant.mainNavigation.corndux.NavigationPerformer
import com.hallett.taskassistant.mainNavigation.corndux.NavigationSideEffectPerformer
import com.hallett.taskassistant.mainNavigation.corndux.NavigationState
import com.hallett.taskassistant.mainNavigation.corndux.NavigationStore
import com.hallett.taskassistant.overdueTasks.corndux.OverduePerformer
import com.hallett.taskassistant.overdueTasks.corndux.OverdueReducer
import com.hallett.taskassistant.overdueTasks.corndux.OverdueStore
import com.hallett.taskassistant.taskList.corndux.TaskListPerformer
import com.hallett.taskassistant.taskList.corndux.TaskListReducer
import com.hallett.taskassistant.taskList.corndux.TaskListStore
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val cornduxModule = DI.Module("cornduxModule"){
    bindSingleton { TaskActionsPerformer(instance(), instance()) }


    bindSingleton { DashboardPerformer(instance(), instance(), instance()) }
    bindSingleton { DashboardReducer() }
    bindSingleton {
        DashboardStore(
            actors = listOf(
                instance<DashboardPerformer>(),
                instance<TaskActionsPerformer>(),
                instance<DashboardReducer>(),
                LoggingMiddleware(),
            ),
            scope = instance()
        )
    }

    bindSingleton { FuturePerformer(instance(), instance()) }
    bindSingleton { FutureReducer() }
    bindSingleton {
        FutureStore(
            actors = listOf(
                instance<FuturePerformer>(),
                instance<TaskActionsPerformer>(),
                instance<FutureReducer>(),
                LoggingMiddleware()
            ),
            scope = instance()
        )
    }

    bindSingleton { CreateTaskPerformer(instance(), instance()) }
    bindSingleton { CreateTaskReducer() }
    bindSingleton {

        CreateTaskStore(
            actors = listOf(
                instance<CreateTaskPerformer>(),
                instance<NavigationSideEffectPerformer>(),
                instance<CreateTaskReducer>(),
                LoggingMiddleware()
            ),
            scope = instance()
        )
    }

    bindSingleton { NavigationPerformer() }
    bindSingleton { NavigationSideEffectPerformer(instance(), instance(), instance()) }
    bindSingleton {
        NavigationStore(
            actors = listOf(
                instance<NavigationPerformer>(),
                instance<NavigationSideEffectPerformer>(),
                LoggingMiddleware()
            ),
            scope = instance()
        )
    }

    bindSingleton { OverduePerformer(instance(), instance()) }
    bindSingleton { OverdueReducer() }
    bindSingleton {
        OverdueStore(
            actors = listOf(
                instance<OverduePerformer>(),
                instance<TaskActionsPerformer>(),
                instance<OverdueReducer>(),
                LoggingMiddleware()
            ),
            scope = instance()
        )
    }

    bindSingleton { TaskListPerformer(instance(), instance(), instance(), instance()) }
    bindSingleton { TaskListReducer() }
    bindSingleton {
        TaskListStore(
            actors = listOf(
                instance<TaskListPerformer>(),
                instance<TaskActionsPerformer>(),
                instance<TaskListReducer>(),
                LoggingMiddleware(),
            ),
            scope = instance()
        )
    }

    bindSingleton<Store<out IState>> { instance<Store<NavigationState>>() }
}

inline fun <reified T: Store<out IState>> DI.MainBuilder.overrideStoreType() {
    bindSingleton<Store<out IState>>(overrides = true) { instance<T>() }
}