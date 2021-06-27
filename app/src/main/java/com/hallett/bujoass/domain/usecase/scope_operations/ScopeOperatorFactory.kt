package com.hallett.bujoass.domain.usecase.scope_operations

import com.hallett.bujoass.domain.model.DScope
import java.util.*

class ScopeOperatorFactory: IScopeOperatorFactory {
    override fun create(scope: DScope, rawDate: Date): IScopeOperator = when(scope){
        DScope.DAY -> DayOperator(rawDate)
        DScope.WEEK -> WeekOperator(rawDate)
        DScope.MONTH -> MonthOperator(rawDate)
        DScope.YEAR -> YearOperator(rawDate)
    }
}