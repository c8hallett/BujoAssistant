package com.hallett.bujoass.presentation.ui

import android.app.Application
import com.hallett.bujoass.di.*
import com.hallett.scopes.di.scopeGeneratorModule
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import timber.log.Timber

class BujoAssApplication: Application(), KodeinAware {

    override val kodein: Kodein by lazy {
        Kodein{
            import(androidModule(this@BujoAssApplication))
            import(databaseModule)
            import(useCaseModule)
            import(utilModule)
            import(viewModelModule)
            import(formatterModule)
            import(mapperModule)
            import(scopeGeneratorModule)
        }
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}