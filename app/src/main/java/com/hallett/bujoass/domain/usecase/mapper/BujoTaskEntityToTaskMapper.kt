package com.hallett.bujoass.domain.usecase.mapper

import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task

class BujoTaskEntityToTaskMapper(
    private val scopeMapper: Mapper<BujoTaskEntity.ScopeInfo?, PScopeInstance>
): Mapper<BujoTaskEntity, Task> {
    override fun map(input: BujoTaskEntity): Task =
        Task(
            id = input.id,
            taskName = input.taskName,
            scope = scopeMapper.map(input.scopeInfo),
            status = input.status
        )
}