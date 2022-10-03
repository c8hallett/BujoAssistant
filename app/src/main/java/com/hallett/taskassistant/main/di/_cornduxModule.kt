package com.hallett.taskassistant.main.di

import com.hallett.taskassistant.features.createTasks.corndux.CreateTaskPerformer
import com.hallett.taskassistant.features.createTasks.corndux.CreateTaskReducer
import com.hallett.taskassistant.features.createTasks.corndux.CreateTaskStore
import com.hallett.taskassistant.features.dashboard.corndux.DashboardPerformer
import com.hallett.taskassistant.features.dashboard.corndux.DashboardReducer
import com.hallett.taskassistant.features.dashboard.corndux.DashboardStore
import com.hallett.taskassistant.features.genericTaskList.TaskActionsPerformer
import com.hallett.taskassistant.features.limboTasks.corndux.LimboPerformer
import com.hallett.taskassistant.features.limboTasks.corndux.LimboReducer
import com.hallett.taskassistant.features.limboTasks.corndux.LimboStore
import com.hallett.taskassistant.features.overdueTasks.corndux.OverduePerformer
import com.hallett.taskassistant.features.overdueTasks.corndux.OverdueReducer
import com.hallett.taskassistant.features.overdueTasks.corndux.OverdueStore
import com.hallett.taskassistant.features.taskList.corndux.TaskListPerformer
import com.hallett.taskassistant.features.taskList.corndux.TaskListReducer
import com.hallett.taskassistant.features.taskList.corndux.TaskListStore
import com.hallett.taskassistant.main.corndux.GlobalStore
import com.hallett.taskassistant.main.corndux.NavigationActor
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val cornduxModule = DI.Module("cornduxModule") {
    bindSingleton { TaskActionsPerformer(instance(), instance(), instance()) }

    bindSingleton { DashboardPerformer(instance(), instance(), instance()) }
    bindSingleton { DashboardReducer() }
    bindSingleton { DashboardStore(instance(), instance(), instance(), instance()) }

    bindSingleton { LimboPerformer(instance(), instance(), instance()) }
    bindSingleton { LimboReducer() }
    bindSingleton { LimboStore(instance(), instance(), instance(), instance()) }

    bindSingleton { CreateTaskPerformer(instance(), instance(), instance()) }
    bindSingleton { CreateTaskReducer() }
    bindSingleton { CreateTaskStore(instance(), instance(), instance()) }

    bindSingleton { OverduePerformer(instance(), instance(), instance()) }
    bindSingleton { OverdueReducer() }
    bindSingleton { OverdueStore(instance(), instance(), instance(), instance()) }

    bindSingleton { TaskListPerformer(instance(), instance(), instance(), instance()) }
    bindSingleton { TaskListReducer() }
    bindSingleton { TaskListStore(instance(), instance(), instance(), instance()) }

    bindSingleton { NavigationActor() }
    bindSingleton { GlobalStore(instance(), instance()) }
}
