package com.hallett.bujoass.usecase

import com.hallett.bujoass.database.BujoTaskDao
import com.hallett.bujoass.database.BujoTaskEntity
import com.hallett.bujoass.domain.DomainScope
import java.util.*

class SaveNewTaskUseCase(
    private val taskDao: BujoTaskDao
): ISaveNewTaskUseCase {
    override suspend fun execute(task: String, scope: DomainScope?, scopeDate: Date) {
        val scopeInfo = when(scope) {
            null -> null
            else -> BujoTaskEntity.ScopeInfo(scope, scopeDate)
        }
        val newEntity = BujoTaskEntity(
            taskName = task,
            scopeInfo = scopeInfo,
            createdAt = Date(),
            updatedAt = Date(),
        )
        taskDao.upsert(newEntity)
    }
}