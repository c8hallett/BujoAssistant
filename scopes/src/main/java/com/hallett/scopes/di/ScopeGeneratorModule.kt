package com.hallett.scopes.di

import com.hallett.scopes.scope_evaluator.IScopeEvaluator
import com.hallett.scopes.scope_evaluator.ScopeEvaluator
import com.hallett.scopes.scope_generator.IScopeGenerator
import com.hallett.scopes.scope_generator.ScopeGenerator
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton


val scopeGeneratorModule = DI.Module("scope_generator_module"){
    bind<IScopeGenerator>() with singleton { ScopeGenerator() }
    bind<IScopeEvaluator>() with singleton { ScopeEvaluator() }
}