package com.hallett.bujoass.di

import com.hallett.bujoass.domain.usecase.modify_task.*
import com.hallett.bujoass.domain.usecase.observe_task.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

@ExperimentalCoroutinesApi
val useCaseModule = Kodein.Module("use_case_module") {
    bind<ISaveNewTaskUseCase>() with singleton { SaveNewTaskUseCase(instance()) }
    bind<IObserveTaskListUseCase>() with singleton { ObserveTaskListUseCase(instance(), instance()) }
    bind<IObserveTaskListFlowableUseCase>() with singleton { ObserveTaskListFlowableUseCase(instance()) }
    bind<IObserveCurrentTasksFlowableUseCase>() with singleton { ObserveCurrentTasksFlowableUseCase(instance(), instance(), instance()) }
    bind<IObserveSingleTaskUseCase>() with singleton { ObserveSingleTaskUseCase(instance(), instance()) }
    bind<IDeleteTaskUseCase>() with singleton { DeleteTaskUseCase(instance()) }
    bind<IModifyTaskStatusUseCase>() with singleton { ModifyTaskStatusUseCase(instance()) }
    bind<IRescheduleTaskUseCase>() with singleton { RescheduleTaskUseCase(instance()) }
    bind<IDeferTaskUseCase>() with singleton { DeferTaskUseCase(instance(), instance()) }
}