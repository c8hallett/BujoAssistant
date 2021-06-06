package com.hallett.bujoass.di

import com.hallett.bujoass.domain.usecase.INormalizeDateForScopeUseCase
import com.hallett.bujoass.domain.usecase.ISaveNewTaskUseCase
import com.hallett.bujoass.domain.usecase.NormalizeDateForScopeUseCase
import com.hallett.bujoass.domain.usecase.SaveNewTaskUseCase
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val useCaseModule = Kodein.Module("use_case_module") {
    bind<ISaveNewTaskUseCase>() with singleton { SaveNewTaskUseCase(instance(), instance()) }
    bind<INormalizeDateForScopeUseCase>() with singleton { NormalizeDateForScopeUseCase() }
}