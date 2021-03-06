package com.hallett.taskassistant.di

import androidx.compose.ui.unit.Dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.corndux.Actor
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.IStore
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TaskAssistantStore
import com.hallett.taskassistant.corndux.middleware.LoggingMiddleware
import com.hallett.taskassistant.corndux.performers.CreateTaskScreenPerformer
import com.hallett.taskassistant.corndux.performers.DashboardScreenPerformer
import com.hallett.taskassistant.corndux.performers.FutureTaskPerformer
import com.hallett.taskassistant.corndux.performers.OverdueTaskPerformer
import com.hallett.taskassistant.corndux.performers.RootNavigationPerformer
import com.hallett.taskassistant.corndux.performers.TaskActionPerformer
import com.hallett.taskassistant.corndux.performers.TaskListPerformer
import com.hallett.taskassistant.corndux.performers.utils.ScopeSelectionInfoGenerator
import com.hallett.taskassistant.corndux.performers.utils.TaskListTransformer
import com.hallett.taskassistant.corndux.reducers.ComponentReducer
import com.hallett.taskassistant.corndux.reducers.SessionReducer
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
            CreateTaskScreenPerformer(instance(), instance()),
            DashboardScreenPerformer(instance(), instance(), instance()),
            FutureTaskPerformer(instance(), instance()),
            OverdueTaskPerformer(instance(), instance()),
            RootNavigationPerformer(),
            TaskActionPerformer(instance(), instance()),
            TaskListPerformer(instance(), instance(), instance(), instance()),
            LoggingMiddleware(),
            ComponentReducer(),
            SessionReducer()
        )
    }
    bindSingleton<ScopeSelectionInfoGenerator> {
        ScopeSelectionInfoGenerator(factory(), instance())
    }
    bindSingleton<TaskListTransformer> {
        TaskListTransformer(instance(), instance(tag = Formatter.SIMPLE_LABEL))
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