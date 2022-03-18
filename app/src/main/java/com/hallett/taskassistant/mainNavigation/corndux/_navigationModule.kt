package com.hallett.taskassistant.mainNavigation.corndux

import com.hallett.corndux.Actor
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val navigationModule = DI.Module("navigationModule"){
    bindSingleton {
        NavigationStore(
            actors = instance(),
            scope = instance()
        )
    }
    bindSingleton<List<Actor<out NavigationState>>> {
        listOf(
            NavigationPerformer(),
            NavigationSideEffectPerformer(instance())
        )
    }
}