package com.hallett.taskassistant.taskdashboard.corndux

import com.hallett.corndux.IAction

sealed interface DashboardAction: IAction
object FetchInitialData: DashboardAction
object LoadLargerScope: DashboardAction
object LoadSmallerScope: DashboardAction