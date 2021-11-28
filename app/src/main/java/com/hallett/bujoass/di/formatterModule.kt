package com.hallett.bujoass.di

import com.hallett.bujoass.presentation.format.Formatter
import com.hallett.bujoass.presentation.format.MediumDateFormatter
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

val formatterModule = Kodein.Module("formatter_module"){
    bind<Formatter<Scope?>>() with singleton { MediumDateFormatter() }
}