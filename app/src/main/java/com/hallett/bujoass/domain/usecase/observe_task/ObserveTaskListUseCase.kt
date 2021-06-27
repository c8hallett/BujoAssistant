package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.domain.usecase.scope_operations.ICheckIfScopeInstanceIsCurrentUseCase
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ObserveTaskListUseCase(
    private val taskDao: BujoTaskDao,
    private val scopeMapper: Mapper<PScopeInstance, DScopeInstance?>,
    private val checkICheckIfScopeInstanceIsCurrentUseCase: ICheckIfScopeInstanceIsCurrentUseCase,
): IObserveTaskListUseCase {
    override fun execute(scope: PScopeInstance): Flow<List<Task>> {
        val dScope = scopeMapper.map(scope)
        Timber.i("Returning flowable for ${dScope?.scope} at ${dScope?.date?.time}")
        val isCurrent = when(dScope){
            null -> false
            else -> checkICheckIfScopeInstanceIsCurrentUseCase.execute(dScope)
        }
        return taskDao.getAllTaskForScopeInstance(dScope?.scope, dScope?.date).map { list ->
            list.map {
                Task(
                    id = it.id,
                    taskName = it.taskName,
                    scope = scope,
                    status = it.status,
                    isCurrentScope = isCurrent
                )
            }
        }
    }
}