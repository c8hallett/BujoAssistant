package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.scopes.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class SaveNewTaskUseCase(
    private val taskDao: BujoTaskDao,
): ISaveNewTaskUseCase {
    override suspend fun execute(taskName: String, scope: Scope?) = withContext(Dispatchers.IO) {
        if(taskName.isBlank()) throw IllegalArgumentException("Task name cannot be blank")

        val newEntity = BujoTaskEntity(
            taskName = taskName,
            scope = scope
        )
        taskDao.upsert(newEntity)
    }
}