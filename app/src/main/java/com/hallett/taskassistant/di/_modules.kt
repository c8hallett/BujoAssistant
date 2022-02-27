package com.hallett.taskassistant.di

import androidx.compose.ui.unit.Dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.corndux.SideEffectPerformer
import com.hallett.corndux.Store
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeGenerator
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.IMiddleware
import com.hallett.taskassistant.corndux.LoggingMiddleware
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.actionperformers.CreateTaskActionPerformer
import com.hallett.taskassistant.corndux.actionperformers.RootNavigationActionPerformer
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantSideEffectPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TaskAssistantStore
import com.hallett.taskassistant.corndux.actionperformers.OverdueTaskActionPerformer
import com.hallett.taskassistant.corndux.actionperformers.SelectScopeActionPerformer
import com.hallett.taskassistant.corndux.actionperformers.TaskListActionPerformer
import com.hallett.taskassistant.ui.formatters.Formatter
import com.hallett.taskassistant.ui.formatters.ScopeOffsetLabelFormatter
import com.hallett.taskassistant.ui.formatters.ScopeScaleFormatter
import com.hallett.taskassistant.ui.formatters.ScopeSimpleDateFormatter
import com.hallett.taskassistant.ui.formatters.ScopeSimpleLabelFormatter
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
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
    bindSingleton<Store<TaskAssistantState, TaskAssistantAction, TaskAssistantSideEffect>> {
        val initialState = TaskAssistantState(
            screen = TaskNavDestination.TaskList,
            scope = null,
            scopeSelectionInfo = null,
            tasks = null,
            error = null,
        )

        TaskAssistantStore(
            initialState = initialState,
            performers = instance(),
            middlewares = instance(),
            sideEffectPerformer = instance(),
            scope = instance()
        )
    }

    bindSingleton<List<IMiddleware>> {
        listOf(
            LoggingMiddleware()
        )
    }

    bindSingleton<List<IActionPerformer>> {
        listOf(
            SelectScopeActionPerformer(factory(), instance()),
            CreateTaskActionPerformer(instance()),
            OverdueTaskActionPerformer(instance()),
            TaskListActionPerformer(instance(), instance()),
            RootNavigationActionPerformer(),
        )
    }

    bindSingleton<SideEffectPerformer<TaskAssistantSideEffect>> {
        TaskAssistantSideEffectPerformer(instance(), instance())
    }
}

data class PagerParams(val config: PagingConfig, val scopeType: ScopeType)

val pagingModule = DI.Module("paging_module") {
    bindFactory<PagerParams, Pager<Scope, Scope>> { params: PagerParams ->
        val initialKey = instance<IScopeGenerator>().generateScope(params.scopeType)
        Pager(params.config, initialKey) { ScopePagingSource(instance(), instance()) }
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