package com.hallett.bujoass.domain.usecase.mapper

import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance

class ScopeInfoToPScopeInstanceMapper(
    private val scopeMapper: Mapper<DScope?, PScope>,
): Mapper<BujoTaskEntity.ScopeInfo?, PScopeInstance> {
    override fun map(input: BujoTaskEntity.ScopeInfo?): PScopeInstance =
        when(val pScope = scopeMapper.map(input?.taskScope)){
            PScope.NONE -> PScopeInstance.NONE
            else -> when(val date = input?.scopeValue) {
                null -> PScopeInstance.NONE
                else -> PScopeInstance(pScope, date)
            }
        }
}