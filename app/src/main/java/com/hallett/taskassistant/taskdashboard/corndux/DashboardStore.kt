package com.hallett.taskassistant.taskdashboard.corndux

import com.hallett.corndux.ActionPerformer
import com.hallett.corndux.Middleware
import com.hallett.corndux.Store
import kotlinx.coroutines.CoroutineScope

class DashboardStore(
    initialState: DashboardState,
    performers: List<IDashboardActionPerformer>,
    middlewares: List<IDashboardMiddleware>,
    scope: CoroutineScope
): IDashboardStore (
    initialState = initialState,
    performers = performers,
    middlewares = middlewares,
    scope = scope
) {
}

typealias IDashboardActionPerformer = ActionPerformer<DashboardState, DashboardAction, Nothing>
typealias IDashboardMiddleware = Middleware<DashboardState, DashboardAction>
typealias IDashboardStore = Store<DashboardState, DashboardAction, Nothing>
