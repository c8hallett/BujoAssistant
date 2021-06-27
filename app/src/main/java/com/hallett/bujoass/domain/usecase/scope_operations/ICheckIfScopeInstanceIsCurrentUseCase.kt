package com.hallett.bujoass.domain.usecase.scope_operations

import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import java.util.*

interface ICheckIfScopeInstanceIsCurrentUseCase {
    fun execute(instance: DScopeInstance): Boolean
    fun execute(scope: DScope, date: Date): Boolean
}