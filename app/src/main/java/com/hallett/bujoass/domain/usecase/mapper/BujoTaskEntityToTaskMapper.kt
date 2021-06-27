package com.hallett.bujoass.domain.usecase.mapper

import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.usecase.scope_operations.ICheckIfScopeInstanceIsCurrentUseCase
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task

class BujoTaskEntityToTaskMapper(
    private val scopeMapper: Mapper<BujoTaskEntity.ScopeInfo?, PScopeInstance>,
    private val checkIfScopeInstanceIsCurrentUseCase: ICheckIfScopeInstanceIsCurrentUseCase
): Mapper<BujoTaskEntity, Task> {
    override fun map(input: BujoTaskEntity): Task = with(input){
        val isCurrent = when(scopeInfo) {
            null -> false
            else -> checkIfScopeInstanceIsCurrentUseCase.execute(scopeInfo.taskScope, scopeInfo.scopeValue)
        }
        Task(
            id = id,
            taskName = taskName,
            scope = scopeMapper.map(scopeInfo),
            status = status,
            isCurrentScope = isCurrent
        )
    }
}