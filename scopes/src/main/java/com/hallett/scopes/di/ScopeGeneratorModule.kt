package com.hallett.scopes.di

import com.hallett.scopes.IScopeGenerator
import com.hallett.scopes.ScopeGenerator
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton


val scopeGeneratorModule = Kodein.Module("scope_generator_module"){
    bind<IScopeGenerator>() with singleton { ScopeGenerator() }
}