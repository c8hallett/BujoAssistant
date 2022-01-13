package com.hallett.taskassistant.ui

import android.app.Application
import com.hallett.scopes.di.scopeGeneratorModule
import com.hallett.taskassistant.di.formatterModule
import com.hallett.taskassistant.di.pagingModule
import com.hallett.taskassistant.di.viewModelModule
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule

class MainApplication: Application(), KodeinAware {
    override val kodein: Kodein by lazy {
        Kodein {
            import(androidModule(this@MainApplication))
            import(viewModelModule)
            import(formatterModule)
            import(pagingModule)
            import(scopeGeneratorModule)
        }
    }
}