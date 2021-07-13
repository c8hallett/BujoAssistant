package com.hallett.bujoass.domain.usecase.mapper

import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.presentation.model.Task

class BujoTaskEntityToTaskMapper: Mapper<BujoTaskEntity, Task> {
    override fun map(input: BujoTaskEntity): Task = with(input){
        Task(
            id = id,
            taskName = taskName,
            scope = scope,
            status = status,
        )
    }
}