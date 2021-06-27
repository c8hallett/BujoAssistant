package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.usecase.scope_operations.IScopeOperator
import java.lang.IllegalArgumentException
import java.util.*

class DeferTaskUseCase(
    private val taskDao: BujoTaskDao,
    private val getOperator: (DScope, Date) -> IScopeOperator,
): IDeferTaskUseCase {
    override suspend fun execute(taskId: Long) {
        when(val existingTask = taskDao.getTask(taskId)) {
            null -> throw IllegalArgumentException("Requested task #$taskId does not exist, cannot defer")
            else -> when(val scopeInfo = existingTask.scopeInfo) {
                null -> throw IllegalStateException("Requested task #$taskId is not scheduled, cannot defer")
                else -> {
                    val operator = getOperator(scopeInfo.taskScope, scopeInfo.scopeValue)
                    val newScopeInfo = with(operator.add(1)) {
                        BujoTaskEntity.ScopeInfo(type, date)
                    }
                    val update = BujoTaskEntity.NewScopeUpdate(taskId, newScopeInfo)
                    taskDao.rescheduleTask(update)
                }
            }
        }

    }
}