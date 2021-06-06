package com.hallett.bujoass.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instanceOrNull

class KodeinViewModelProviderFactory(override val kodein: Kodein): ViewModelProvider.Factory, KodeinAware {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return instanceOrNull<ViewModel>(tag = modelClass) as? T ?: modelClass.newInstance()
    }
}