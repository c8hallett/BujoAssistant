package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.usecase.scope_operations.IScopeOperator
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.factory
import org.kodein.di.generic.factory2
import org.kodein.di.generic.instance
import java.lang.IllegalArgumentException
import java.util.*

class DeferTaskUseCase(
    override val kodein: Kodein,
): IDeferTaskUseCase, KodeinAware {
    private val taskDao: BujoTaskDao by instance()
    private val getOperator: (DScope, Date) -> IScopeOperator by factory2()

    data class IScopeParams(val scope: DScope, val date: Date)
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