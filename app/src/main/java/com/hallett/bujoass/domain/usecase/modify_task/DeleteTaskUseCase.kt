package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.database.task.BujoTaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteTaskUseCase(
    private val dao: BujoTaskDao,
): IDeleteTaskUseCase {
    override suspend fun execute(taskId: Long) = withContext(Dispatchers.IO){
        dao.delete(taskId)
    }
}