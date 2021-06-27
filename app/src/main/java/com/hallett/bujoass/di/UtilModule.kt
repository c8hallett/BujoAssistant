package com.hallett.bujoass.di

import androidx.lifecycle.ViewModelProvider
import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.domain.usecase.mapper.*
import com.hallett.bujoass.domain.usecase.scope_operations.*
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.util.*

val utilModule = Kodein.Module("util_module") {
    bind<ViewModelProvider.Factory>() with singleton { KodeinViewModelProviderFactory(kodein) }
    bind<IScopeOperator>() with factory { scope: DScope, rawDate: Date ->
        when(scope){
            DScope.DAY -> DayOperator(rawDate)
            DScope.WEEK -> WeekOperator(rawDate)
            DScope.MONTH -> MonthOperator(rawDate)
            DScope.YEAR -> YearOperator(rawDate)
        }
    }
}