package com.hallett.bujoass.usecase.mapper

import com.hallett.bujoass.domain.DomainScope
import com.hallett.bujoass.presentation.PresentationScope

class DomainToPresentationScopeMapper: Mapper<DomainScope?, PresentationScope> {
    override fun map(input: DomainScope?): PresentationScope {
        return when(input){
            null -> PresentationScope.NONE
            else -> PresentationScope.values()[input.ordinal + 1]
        }
    }
}