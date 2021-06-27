package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class ObserveSingleTaskUseCase(
    private val taskDao: BujoTaskDao,
    private val taskMapper: Mapper<BujoTaskEntity, Task>,
): IObserveSingleTaskUseCase {
    override fun execute(id: Long): Flow<Task> = taskDao.observeTask(id).map {
        when(it) {
            // how do we handle when currently observed task gets deleted? or never existed?
            null -> throw IllegalStateException("Requested task #$id no longer exists")
            else -> taskMapper.map(it)
        }
    }
}