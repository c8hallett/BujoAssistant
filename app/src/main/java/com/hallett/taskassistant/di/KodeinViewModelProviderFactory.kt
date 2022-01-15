package com.hallett.taskassistant.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.DirectDI
import org.kodein.di.DirectDIAware
import org.kodein.di.instanceOrNull

class KodeinViewModelProviderFactory(override val directDI: DirectDI): ViewModelProvider.Factory, DirectDIAware {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return directDI.instanceOrNull<ViewModel>(tag = modelClass) as? T ?: modelClass.newInstance()
    }
}