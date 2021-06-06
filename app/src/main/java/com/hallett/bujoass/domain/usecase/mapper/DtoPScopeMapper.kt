package com.hallett.bujoass.domain.usecase.mapper

import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.presentation.model.PScope

class DtoPScopeMapper: Mapper<DScope?, PScope> {
    override fun map(input: DScope?): PScope {
        return when(input){
            null -> PScope.NONE
            else -> PScope.values()[input.ordinal + 1]
        }
    }
}