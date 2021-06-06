package com.hallett.bujoass.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
import org.kodein.di.generic.instanceOrNull

class KodeinViewModelProviderFactory(override val kodein: Kodein): ViewModelProvider.Factory, KodeinAware {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return kodein.direct.instanceOrNull<ViewModel>(tag = modelClass.simpleName) as? T ?: modelClass.newInstance()
    }
}