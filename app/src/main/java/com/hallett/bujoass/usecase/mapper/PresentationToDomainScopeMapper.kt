package com.hallett.bujoass.usecase.mapper

import com.hallett.bujoass.domain.DomainScope
import com.hallett.bujoass.presentation.PresentationScope

class PresentationToDomainScopeMapper: Mapper<PresentationScope, DomainScope?> {
    override fun map(input: PresentationScope): DomainScope? {
        return when(input){
            PresentationScope.NONE -> null
            else -> DomainScope.values()[input.ordinal - 1]
        }
    }
}