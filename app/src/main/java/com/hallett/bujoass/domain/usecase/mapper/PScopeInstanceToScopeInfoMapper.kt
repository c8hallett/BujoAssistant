package com.hallett.bujoass.domain.usecase.mapper

import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.usecase.scope_manipulation.INormalizeDateForScopeUseCase
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance

class PScopeInstanceToScopeInfoMapper(
    private val scopeMapper: Mapper<PScope, DScope?>,
    private val normalizeDateForScopeUseCase: INormalizeDateForScopeUseCase,
): Mapper<PScopeInstance, BujoTaskEntity.ScopeInfo?> {
    override fun map(input: PScopeInstance): BujoTaskEntity.ScopeInfo? =
        when(val dScope = scopeMapper.map(input.type)){
            null -> null
            else -> BujoTaskEntity.ScopeInfo(
                dScope,
                normalizeDateForScopeUseCase.execute(dScope, input.date)
            )
        }
}