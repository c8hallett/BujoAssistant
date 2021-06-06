package com.hallett.bujoass.domain.usecase

import com.hallett.bujoass.database.BujoTaskDao
import com.hallett.bujoass.database.BujoTaskEntity
import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.presentation.model.PScopeInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.util.*

class SaveNewTaskUseCase(
    private val taskDao: BujoTaskDao,
    private val scopeInstanceMapper: Mapper<PScopeInstance, DScopeInstance?>,
): ISaveNewTaskUseCase {
    override suspend fun execute(taskName: String, scopeInstance: PScopeInstance) = withContext(Dispatchers.IO) {
        if(taskName.isBlank()){
            throw IllegalArgumentException("Task name cannot be blank")
        }

        val scopeInfo = when(val dScope = scopeInstanceMapper.map(scopeInstance)) {
            null -> null
            else -> BujoTaskEntity.ScopeInfo(dScope.type, dScope.date)
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