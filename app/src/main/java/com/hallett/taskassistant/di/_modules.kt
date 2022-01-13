package com.hallett.taskassistant.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeGenerator
import com.hallett.taskassistant.ui.TaskAssistantViewModel
import com.hallett.taskassistant.ui.formatters.IFormatter
import com.hallett.taskassistant.ui.formatters.SelectableScopeFormatter
import com.hallett.taskassistant.ui.model.scope.SelectableScope
import com.hallett.taskassistant.ui.paging.ScopePagingSource
import org.kodein.di.Kodein
import org.kodein.di.generic.*

val viewModelModule = Kodein.Module("viewmodel_module") {

    bind<ViewModelProvider.Factory>() with singleton { KodeinViewModelProviderFactory(kodein) }
    bind<ViewModel>(tag = TaskAssistantViewModel::class.java) with provider { TaskAssistantViewModel(instance(), kodein) }
}

val formatterModule = Kodein.Module("formatter_module") {
    bind<IFormatter<Scope, SelectableScope>>() with singleton { SelectableScopeFormatter(instance()) }
}

val pagingModule = Kodein.Module("paging_module") {
    bind<Pager<Scope, Scope>>() with factory { config: PagingConfig, scopeType: ScopeType ->
        val initialKey = instance<IScopeGenerator>().generateScope(scopeType)
        Pager(config, initialKey) {
            ScopePagingSource(instance(), instance())
        }
    }
}