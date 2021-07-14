package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ObserveTaskListUseCase(
    private val taskDao: BujoTaskDao,
    private val taskMapper: Mapper<BujoTaskEntity, Task>
): IObserveTaskListUseCase {
    override fun execute(scope: Scope?): Flow<List<Task>> {
        return taskDao.getAllTaskForScopeInstance(scope).map { list ->
            list.map { taskMapper.map(it) }
        }
    }
}