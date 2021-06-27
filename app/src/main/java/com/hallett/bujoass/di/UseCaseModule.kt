package com.hallett.bujoass.di

import com.hallett.bujoass.domain.usecase.modify_task.*
import com.hallett.bujoass.domain.usecase.observe_task.*
import com.hallett.bujoass.domain.usecase.scope_operations.CheckIfScopeInstanceIsCurrentUseCase
import com.hallett.bujoass.domain.usecase.scope_operations.ICheckIfScopeInstanceIsCurrentUseCase
import com.hallett.bujoass.domain.usecase.scope_operations.INormalizeDateForScopeUseCase
import com.hallett.bujoass.domain.usecase.scope_operations.NormalizeDateForScopeUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

@ExperimentalCoroutinesApi
val useCaseModule = Kodein.Module("use_case_module") {
    bind<ISaveNewTaskUseCase>() with singleton { SaveNewTaskUseCase(instance(), instance()) }
    bind<IObserveTaskListUseCase>() with singleton { ObserveTaskListUseCase(instance(), instance(), instance()) }
    bind<IObserveTaskListFlowableUseCase>() with singleton { ObserveTaskListFlowableUseCase(instance()) }
    bind<IObserveSingleTaskUseCase>() with singleton { ObserveSingleTaskUseCase(instance(), instance()) }
    bind<IDeleteTaskUseCase>() with singleton { DeleteTaskUseCase(instance()) }
    bind<IModifyTaskStatusUseCase>() with singleton { ModifyTaskStatusUseCase(instance()) }
    bind<IRescheduleTaskUseCase>() with singleton { RescheduleTaskUseCase(instance(), instance()) }
    bind<IDeferTaskUseCase>() with singleton { DeferTaskUseCase(kodein) }

    bind<INormalizeDateForScopeUseCase>() with singleton { NormalizeDateForScopeUseCase() }
    bind<ICheckIfScopeInstanceIsCurrentUseCase>() with singleton { CheckIfScopeInstanceIsCurrentUseCase(instance()) }
}