package com.hallett.taskassistant.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hallett.scopes.Scope
import com.hallett.taskassistant.ui.TaskAssistantViewModel
import com.hallett.taskassistant.ui.formatters.IFormatter
import com.hallett.taskassistant.ui.formatters.SelectableScopeFormatter
import com.hallett.taskassistant.ui.model.SelectableScope
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

val viewModelModule = Kodein.Module("viewmodel") {

    bind<ViewModelProvider.Factory>() with singleton { KodeinViewModelProviderFactory(kodein) }
    bind<ViewModel>(tag = TaskAssistantViewModel::class.java) with provider { TaskAssistantViewModel(instance(), instance()) }
}

val formatterModule = Kodein.Module("formatter") {
    bind<IFormatter<Scope, SelectableScope>>() with singleton { SelectableScopeFormatter(instance()) }
}