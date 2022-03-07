package com.hallett.taskassistant.di

import androidx.compose.ui.unit.Dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.corndux.Actor
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.IReducer
import com.hallett.taskassistant.corndux.IStore
import com.hallett.taskassistant.corndux.actors.LoggingMiddleware
import com.hallett.taskassistant.corndux.actors.RootNavigationActor
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TaskAssistantStore
import com.hallett.taskassistant.corndux.actors.DashboardScreenActor
import com.hallett.taskassistant.corndux.actors.CreateTaskScreenActor
import com.hallett.taskassistant.corndux.actors.OverdueTaskActor
import com.hallett.taskassistant.corndux.actors.ScopeSelectionActor
import com.hallett.taskassistant.corndux.actors.TaskListPerformer
import com.hallett.taskassistant.corndux.sideeffects.NavigationSideEffectPerformer
import com.hallett.taskassistant.ui.formatters.Formatter
import com.hallett.taskassistant.ui.formatters.ScopeOffsetLabelFormatter
import com.hallett.taskassistant.ui.formatters.ScopeScaleFormatter
import com.hallett.taskassistant.ui.formatters.ScopeSimpleDateFormatter
import com.hallett.taskassistant.ui.formatters.ScopeSimpleLabelFormatter
import com.hallett.taskassistant.ui.paging.ScopePagingSource
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindSingleton
import org.kodein.di.compose.instance
import org.kodein.di.factory
import org.kodein.di.instance

val formatterModule = DI.Module("formatter_module") {
    bindSingleton<Formatter<Scope?, Dp>>(tag = Formatter.EXTRA_PADDING) { ScopeScaleFormatter() }
    bindSingleton<Formatter<Scope?, String>>(tag = Formatter.OFFSET_LABEL) {
        ScopeOffsetLabelFormatter(
            instance()
        )
    }
    bindSingleton<Formatter<Scope?, String>>(tag = Formatter.SIMPLE_DATE) { ScopeSimpleDateFormatter() }
    bindSingleton<Formatter<Scope?, String>>(tag = Formatter.SIMPLE_LABEL) {
        ScopeSimpleLabelFormatter(
            instance()
        )
    }
}

val cornduxModule = DI.Module("corndux_module") {
    bindSingleton<IStore> {
        TaskAssistantStore(
            initialState = TaskAssistantState(),
            actors = instance(),
            scope = instance()
        )
    }

    bindSingleton<List<Actor<out TaskAssistantState>>> {
        listOf(
            CreateTaskScreenActor(instance()),
            DashboardScreenActor(instance(), instance()),
            LoggingMiddleware(),
            OverdueTaskActor(instance()),
            RootNavigationActor(),
            ScopeSelectionActor(factory(), instance()),
            TaskListPerformer(instance(), instance()),
            NavigationSideEffectPerformer(instance(), instance(), instance())
        )
    }
}

data class PagerParams(
    val config: PagingConfig,
    val scopeType: ScopeType,
    val allowPreviousScopes: Boolean = false
)

val pagingModule = DI.Module("paging_module") {
    bindFactory<PagerParams, Pager<Scope, Scope>> { params: PagerParams ->
        val initialKey = instance<IScopeCalculator>().generateScope(params.scopeType)
        Pager(params.config, initialKey) {
            ScopePagingSource(instance(), params.allowPreviousScopes)
        }
    }
}

val utilModule = DI.Module("util_module") {
    bindSingleton<DispatchersWrapper>() {
        DispatchersWrapper(
            main = Dispatchers.Main,
            io = Dispatchers.IO,
            default = Dispatchers.Default
        )
    }
}