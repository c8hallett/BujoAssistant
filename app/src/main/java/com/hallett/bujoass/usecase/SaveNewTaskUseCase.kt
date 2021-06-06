package com.hallett.bujoass.usecase

import com.hallett.bujoass.database.BujoTaskDao
import com.hallett.bujoass.database.BujoTaskEntity
import com.hallett.bujoass.domain.DomainScope
import com.hallett.bujoass.presentation.PresentationScope
import com.hallett.bujoass.usecase.mapper.Mapper
import java.util.*

class SaveNewTaskUseCase(
    private val taskDao: BujoTaskDao,
    private val scopeMapper: Mapper<PresentationScope, DomainScope?>,
): ISaveNewTaskUseCase {
    override suspend fun execute(task: String, scope: PresentationScope, scopeDate: Date) {
        val scopeInfo = when(val domainScope = scopeMapper.map(scope)) {
            null -> null
            else -> BujoTaskEntity.ScopeInfo(domainScope, scopeDate)
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