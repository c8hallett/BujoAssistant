package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.scopes.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RescheduleTaskUseCase(
    private val taskDao: BujoTaskDao,
): IRescheduleTaskUseCase {
    override suspend fun execute(taskId: Long, scope: Scope?) = withContext(Dispatchers.IO) {
        val update = BujoTaskEntity.NewScopeUpdate(
            taskId,
            scope
        )
        taskDao.rescheduleTask(update)
    }
}