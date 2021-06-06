package com.hallett.bujoass.domain.usecase.mapper

import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.presentation.model.PScope

class PtoDScopeMapper: Mapper<PScope, DScope?> {
    override fun map(input: PScope): DScope? {
        return when(input){
            PScope.NONE -> null
            else -> DScope.values()[input.ordinal - 1]
        }
    }
}