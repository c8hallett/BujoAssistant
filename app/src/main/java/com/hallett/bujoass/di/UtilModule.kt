package com.hallett.bujoass.di

import androidx.lifecycle.ViewModelProvider
import com.hallett.bujoass.domain.DomainScope
import com.hallett.bujoass.presentation.PresentationScope
import com.hallett.bujoass.usecase.mapper.Mapper
import com.hallett.bujoass.usecase.mapper.DomainToPresentationScopeMapper
import com.hallett.bujoass.usecase.mapper.PresentationToDomainScopeMapper
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

val utilModule = Kodein.Module("util_module") {
    bind<ViewModelProvider.Factory>() with singleton { KodeinViewModelProviderFactory(kodein) }

    bind<Mapper<DomainScope?, PresentationScope>>() with singleton { DomainToPresentationScopeMapper() }
    bind<Mapper<PresentationScope, DomainScope?>>() with singleton { PresentationToDomainScopeMapper() }
}