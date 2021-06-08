package com.hallett.bujoass.domain.usecase.mapper

import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.domain.usecase.scope_operations.INormalizeDateForScopeUseCase
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance

class PtoDScopeInstanceMapper(
    private val scopeMapper: Mapper<PScope, DScope?>,
    private val normalizeDateForScopeUseCase: INormalizeDateForScopeUseCase,
): Mapper<PScopeInstance, DScopeInstance?> {
    override fun map(input: PScopeInstance): DScopeInstance? = with(input){
        when(val scope = scopeMapper.map(type)) {
            null -> null
            else -> DScopeInstance(scope, normalizeDateForScopeUseCase.execute(scope, date))
        }
    }
}