package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.scopes.IScopeGenerator
import java.lang.IllegalArgumentException

class DeferTaskUseCase(
    private val taskDao: BujoTaskDao,
    private val scopeGenerator: IScopeGenerator
): IDeferTaskUseCase {

    override suspend fun execute(taskId: Long) {
        when(val existingTask = taskDao.getTask(taskId)) {
            null -> throw IllegalArgumentException("Requested task #$taskId does not exist, cannot defer")
            else -> when(existingTask.scope) {
                null -> throw IllegalStateException("Requested task #$taskId is not scheduled, cannot defer")
                else -> {
                    val update = BujoTaskEntity.NewScopeUpdate(taskId, scopeGenerator.add(existingTask.scope, 1))
                    taskDao.rescheduleTask(update)
                }
            }
        }
    }
}