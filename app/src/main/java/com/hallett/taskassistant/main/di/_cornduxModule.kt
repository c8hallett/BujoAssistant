package com.hallett.taskassistant.main.di

import com.hallett.taskassistant.features.createTasks.corndux.CreateTaskPerformer
import com.hallett.taskassistant.features.createTasks.corndux.CreateTaskReducer
import com.hallett.taskassistant.features.createTasks.corndux.CreateTaskStore
import com.hallett.taskassistant.features.dashboard.corndux.DashboardPerformer
import com.hallett.taskassistant.features.dashboard.corndux.DashboardReducer
import com.hallett.taskassistant.features.dashboard.corndux.DashboardStore
import com.hallett.taskassistant.features.futureTasks.corndux.FuturePerformer
import com.hallett.taskassistant.features.futureTasks.corndux.FutureReducer
import com.hallett.taskassistant.features.futureTasks.corndux.FutureStore
import com.hallett.taskassistant.features.genericTaskList.corndux.TaskActionsPerformer
import com.hallett.taskassistant.features.genericTaskList.corndux.TaskActionsStore
import com.hallett.taskassistant.features.overdueTasks.corndux.OverduePerformer
import com.hallett.taskassistant.features.overdueTasks.corndux.OverdueReducer
import com.hallett.taskassistant.features.overdueTasks.corndux.OverdueStore
import com.hallett.taskassistant.features.taskList.corndux.TaskListPerformer
import com.hallett.taskassistant.features.taskList.corndux.TaskListReducer
import com.hallett.taskassistant.features.taskList.corndux.TaskListStore
import com.hallett.taskassistant.main.corndux.GlobalStore
import com.hallett.taskassistant.main.corndux.NavigationPerformer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val cornduxModule = DI.Module("cornduxModule") {
    bindSingleton { TaskActionsPerformer(instance(), instance(), instance()) }
    bindSingleton { TaskActionsStore(instance(), instance()) }

    bindSingleton { DashboardPerformer(instance(), instance(), instance()) }
    bindSingleton { DashboardReducer() }
    bindSingleton { DashboardStore(instance(), instance(), instance()) }

    bindSingleton { FuturePerformer(instance(), instance()) }
    bindSingleton { FutureReducer() }
    bindSingleton { FutureStore(instance(), instance(), instance()) }

    bindSingleton { CreateTaskPerformer(instance(), instance(), instance()) }
    bindSingleton { CreateTaskReducer() }
    bindSingleton { CreateTaskStore(instance(), instance(), instance()) }

    bindSingleton { OverduePerformer(instance(), instance(), instance()) }
    bindSingleton { OverdueReducer() }
    bindSingleton { OverdueStore(instance(), instance(), instance()) }

    bindSingleton { TaskListPerformer(instance(), instance(), instance(), instance()) }
    bindSingleton { TaskListReducer() }
    bindSingleton { TaskListStore(instance(), instance(), instance()) }


    bindSingleton { NavigationPerformer() }
    bindSingleton { GlobalStore(instance(), instance()) }
}
