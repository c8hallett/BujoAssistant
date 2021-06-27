package com.hallett.bujoass.domain.usecase.mapper

import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance

class DtoPScopeInstanceMapper(
    private val scopeMapper: Mapper<DScope?, PScope>,
): Mapper<DScopeInstance?, PScopeInstance> {
    override fun map(input: DScopeInstance?): PScopeInstance =
        when(val pScope = scopeMapper.map(input?.scope)){
            PScope.NONE -> PScopeInstance.NONE
            else -> when(val date = input?.date) {
                null -> PScopeInstance.NONE
                else -> PScopeInstance(pScope, date)
            }
        }
}