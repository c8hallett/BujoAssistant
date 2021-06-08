package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.presentation.model.PScopeInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RescheduleTaskUseCase(
    private val taskDao: BujoTaskDao,
    private val mapper: Mapper<PScopeInstance, BujoTaskEntity.ScopeInfo?>
): IRescheduleTaskUseCase {
    override suspend fun execute(taskId: Long, scope: PScopeInstance) = withContext(Dispatchers.IO) {
        val update = BujoTaskEntity.NewScopeUpdate(
            taskId,
            mapper.map(scope)
        )
        taskDao.rescheduleTask(update)
    }
}