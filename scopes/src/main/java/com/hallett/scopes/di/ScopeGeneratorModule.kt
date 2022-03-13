package com.hallett.scopes.di

import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.scopes.scope_generator.ScopeCalculator
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton


val scopeGeneratorModule = DI.Module("scope_generator_module") {
    bind<IScopeCalculator>() with singleton { ScopeCalculator() }
}