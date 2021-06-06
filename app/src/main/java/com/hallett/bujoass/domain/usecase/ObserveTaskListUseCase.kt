package com.hallett.bujoass.domain.usecase

import com.hallett.bujoass.database.BujoTaskDao
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveTaskListUseCase(
    private val taskDao: BujoTaskDao,
    private val scopeMapper: Mapper<PScopeInstance, DScopeInstance?>,
): IObserveTaskListUseCase {
    override fun execute(scope: PScopeInstance): Flow<List<Task>> {
        val dScope = scopeMapper.map(scope)
        return taskDao.getAllTaskForScopeInstance(dScope?.type, dScope?.date).map { list ->
            list.map {
                Task(it.taskName, scope)
            }
        }
    }
}