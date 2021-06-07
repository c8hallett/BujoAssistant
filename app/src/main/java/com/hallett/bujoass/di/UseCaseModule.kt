package com.hallett.bujoass.di

import com.hallett.bujoass.domain.usecase.modify_task.ISaveNewTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.SaveNewTaskUseCase
import com.hallett.bujoass.domain.usecase.observe_task.*
import com.hallett.bujoass.domain.usecase.scope_manipulation.INormalizeDateForScopeUseCase
import com.hallett.bujoass.domain.usecase.scope_manipulation.NormalizeDateForScopeUseCase
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val useCaseModule = Kodein.Module("use_case_module") {
    bind<ISaveNewTaskUseCase>() with singleton { SaveNewTaskUseCase(instance(), instance()) }
    bind<INormalizeDateForScopeUseCase>() with singleton { NormalizeDateForScopeUseCase() }
    bind<IObserveTaskListUseCase>() with singleton { ObserveTaskListUseCase(instance(), instance()) }
    bind<IObserveTaskListFlowableUseCase>() with singleton { ObserveTaskListFlowableUseCase(instance()) }
    bind<IObserveSingleTaskUseCase>() with singleton { ObserveSingleTaskUseCase(instance(), instance()) }
}