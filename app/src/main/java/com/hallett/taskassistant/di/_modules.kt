package com.hallett.taskassistant.di

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeGenerator
import com.hallett.taskassistant.ui.formatters.Formatter
import com.hallett.taskassistant.ui.formatters.ScopeOffsetLabelFormatter
import com.hallett.taskassistant.ui.formatters.ScopeScaleFormatter
import com.hallett.taskassistant.ui.formatters.ScopeSimpleDateFormatter
import com.hallett.taskassistant.ui.formatters.ScopeSimpleLabelFormatter
import com.hallett.taskassistant.ui.paging.ScopePagingSource
import com.hallett.taskassistant.ui.viewmodels.OverdueTaskViewModel
import com.hallett.taskassistant.ui.viewmodels.ScopeSelectionViewModel
import com.hallett.taskassistant.ui.viewmodels.TaskEditViewModel
import com.hallett.taskassistant.ui.viewmodels.TaskListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.factory
import org.kodein.di.instance

@OptIn(FlowPreview::class)
@ExperimentalCoroutinesApi
val viewModelModule = DI.Module("viewmodel_module") {
    bindSingleton<ViewModelProvider.Factory> { KodeinViewModelProviderFactory(directDI) }
    bindProvider<ViewModel>(tag = ScopeSelectionViewModel::class.java) {
        ScopeSelectionViewModel(
            factory<PagerParams, Pager<Scope, Scope>>()
        )
    }
    bindProvider<ViewModel>(tag = TaskEditViewModel::class.java) { TaskEditViewModel(instance()) }
    bindProvider<ViewModel>(tag = TaskListViewModel::class.java) {
        TaskListViewModel(
            instance(),
        )
    }
    bindProvider<ViewModel>(tag = OverdueTaskViewModel::class.java) {
        OverdueTaskViewModel(
            instance(),
        )
    }
}

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