package com.hallett.taskassistant.main.di

import androidx.compose.ui.unit.Dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.ui.genericTaskList.TaskListTransformer
import com.hallett.taskassistant.features.scopeSelection.ScopePagingSource
import com.hallett.taskassistant.ui.formatters.Formatter
import com.hallett.taskassistant.ui.formatters.ScopeOffsetLabelFormatter
import com.hallett.taskassistant.ui.formatters.ScopeScaleFormatter
import com.hallett.taskassistant.ui.formatters.ScopeSimpleDateFormatter
import com.hallett.taskassistant.ui.formatters.ScopeSimpleLabelFormatter
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindSingleton
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
    bindSingleton<DispatchersWrapper> {
        DispatchersWrapper(
            main = Dispatchers.Main,
            io = Dispatchers.IO,
            default = Dispatchers.Default
        )
    }
    bindSingleton<TaskListTransformer> {
        TaskListTransformer(instance(), instance(tag = Formatter.SIMPLE_LABEL))
    }
}