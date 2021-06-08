package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ObserveTaskListUseCase(
    private val taskDao: BujoTaskDao,
    private val scopeMapper: Mapper<PScopeInstance, DScopeInstance?>,
): IObserveTaskListUseCase {
    override fun execute(scope: PScopeInstance): Flow<List<Task>> {
        val dScope = scopeMapper.map(scope)
        Timber.i("Returning flowable for ${dScope?.type} at ${dScope?.date?.time}")
        return taskDao.getAllTaskForScopeInstance(dScope?.type, dScope?.date).map { list ->
            list.map {
                Task(
                    id = it.id,
                    taskName = it.taskName,
                    scope = scope,
                    status = it.status
                )
            }
        }
    }
}