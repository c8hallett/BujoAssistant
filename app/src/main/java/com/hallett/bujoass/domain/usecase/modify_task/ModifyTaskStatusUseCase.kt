package com.hallett.bujoass.domain.usecase.modify_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ModifyTaskStatusUseCase(
    private val taskDao: BujoTaskDao
): IModifyTaskStatusUseCase {
    override suspend fun execute(taskId: Long, taskStatus: TaskStatus) = withContext(Dispatchers.IO){
        taskDao.updateTaskStatus(BujoTaskEntity.StatusUpdate(taskId, taskStatus))
    }
}