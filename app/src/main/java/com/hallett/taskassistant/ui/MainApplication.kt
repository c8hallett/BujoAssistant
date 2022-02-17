package com.hallett.taskassistant.ui

import android.app.Application
import com.hallett.scopes.di.scopeGeneratorModule
import com.hallett.taskassistant.di.databaseModule
import com.hallett.taskassistant.di.formatterModule
import com.hallett.taskassistant.di.pagingModule
import com.hallett.taskassistant.di.viewModelModule
import org.kodein.di.DI
import org.kodein.di.DIAware

class MainApplication : Application(), DIAware {
    override val di: DI by DI.lazy {
        DI {
            import(viewModelModule)
            import(databaseModule)
            import(formatterModule)
            import(pagingModule)
            import(scopeGeneratorModule)
        }
    }
}