package com.hallett.scopes.di

import com.hallett.scopes.scope_evaluator.IScopeEvaluator
import com.hallett.scopes.scope_evaluator.ScopeEvaluator
import com.hallett.scopes.scope_generator.IScopeGenerator
import com.hallett.scopes.scope_generator.ScopeGenerator
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton


val scopeGeneratorModule = Kodein.Module("scope_generator_module"){
    bind<IScopeGenerator>() with singleton { ScopeGenerator() }
    bind<IScopeEvaluator>() with singleton { ScopeEvaluator() }
}