package com.hallett.bujoass.domain.usecase.scope_operations

import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import java.util.*

abstract class IScopeOperator {
    abstract val type: DScope
    abstract val truncatedDate: Calendar
    fun toInstance(): DScopeInstance = DScopeInstance(type, truncatedDate.time)
    fun next(): DScopeInstance = add(1)
    fun previous(): DScopeInstance = subtract(1)
    abstract fun add(unit: Int): DScopeInstance
    abstract fun subtract(unit: Int): DScopeInstance
    abstract fun getPosition(startDate: Date): Int
    protected abstract fun truncate(dateToTruncate: Date): Calendar
}