package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.database.BujoTaskDao
import com.hallett.bujoass.database.BujoTaskEntity
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.domain.usecase.observe_task.IObserveSingleTaskUseCase
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveSingleTaskUseCase(
    private val taskDao: BujoTaskDao,
    private val taskMapper: Mapper<BujoTaskEntity, Task>,
): IObserveSingleTaskUseCase {
    override fun execute(id: Long): Flow<Task> = taskDao.observeTask(id).map {
        taskMapper.map(it)
    }
}