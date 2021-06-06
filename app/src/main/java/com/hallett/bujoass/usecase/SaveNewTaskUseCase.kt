package com.hallett.bujoass.usecase

import com.hallett.bujoass.database.BujoTaskDao
import com.hallett.bujoass.database.BujoTaskEntity
import com.hallett.bujoass.domain.DomainScope
import com.hallett.bujoass.presentation.PresentationScope
import com.hallett.bujoass.usecase.mapper.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.util.*

class SaveNewTaskUseCase(
    private val taskDao: BujoTaskDao,
    private val scopeMapper: Mapper<PresentationScope, DomainScope?>,
    private val normalizeDateForScopeUseCase: INormalizeDateForScopeUseCase,
): ISaveNewTaskUseCase {
    override suspend fun execute(taskName: String, scope: PresentationScope, scopeDate: Date) = withContext(Dispatchers.IO) {
        if(taskName.isBlank()){
            throw IllegalArgumentException("Task name cannot be blank")
        }
        val scopeInfo = when(val domainScope = scopeMapper.map(scope)) {
            null -> null
            else -> {
                val normalizedDate = normalizeDateForScopeUseCase.execute(domainScope, scopeDate)
                BujoTaskEntity.ScopeInfo(domainScope, normalizedDate)
            }
        }
        val newEntity = BujoTaskEntity(
            taskName = taskName,
            scopeInfo = scopeInfo,
            createdAt = Date(),
            updatedAt = Date(),
        )
        taskDao.upsert(newEntity)
    }
}