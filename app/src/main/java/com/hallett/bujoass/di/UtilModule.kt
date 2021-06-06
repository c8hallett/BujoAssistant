package com.hallett.bujoass.di

import androidx.lifecycle.ViewModelProvider
import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.domain.usecase.mapper.*
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val utilModule = Kodein.Module("util_module") {
    bind<ViewModelProvider.Factory>() with singleton { KodeinViewModelProviderFactory(kodein) }

    bind<Mapper<DScope?, PScope>>() with singleton { DtoPScopeMapper() }
    bind<Mapper<PScope, DScope?>>() with singleton { PtoDScopeMapper() }
    bind<Mapper<DScopeInstance?, PScopeInstance>>() with singleton { DtoPScopeInstanceMapper(instance()) }
    bind<Mapper<PScopeInstance, DScopeInstance?>>() with singleton { PtoDScopeInstanceMapper(instance(), instance()) }
}