package com.hallett.bujoass.di

import androidx.lifecycle.ViewModelProvider
import com.hallett.bujoass.database.TaskGenerator
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val utilModule = Kodein.Module("util_module") {
    bind<ViewModelProvider.Factory>() with singleton { KodeinViewModelProviderFactory(kodein) }
    bind<TaskGenerator>() with singleton { TaskGenerator(instance()) }
}