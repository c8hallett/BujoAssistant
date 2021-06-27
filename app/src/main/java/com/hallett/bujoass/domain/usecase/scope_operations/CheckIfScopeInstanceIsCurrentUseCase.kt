package com.hallett.bujoass.domain.usecase.scope_operations

import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import java.util.*

class CheckIfScopeInstanceIsCurrentUseCase(
    private val normalizeDateForScopeUseCase: INormalizeDateForScopeUseCase
) : ICheckIfScopeInstanceIsCurrentUseCase {

    private val todayMap: Map<DScope, Date> by lazy {
        val currentDate = Date()
        DScope.values().associate {
            Pair(it, normalizeDateForScopeUseCase.execute(it, currentDate))
        }
    }

    override fun execute(instance: DScopeInstance): Boolean =
        instance.date == todayMap[instance.type]

    override fun execute(scope: DScope, date: Date): Boolean =
        normalizeDateForScopeUseCase.execute(scope, date) == todayMap[scope]
}