package com.hallett.bujoass.domain.usecase.scope_operations

import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Calendar

class YearOperator(rawDate: Date): IScopeOperator() {
    override val truncatedDate: Calendar = truncate(rawDate)
    override val type: DScope = DScope.YEAR

    override fun add(unit: Int): DScopeInstance {
        val forwardedDate = (truncatedDate.clone() as Calendar).apply {
            add(Calendar.YEAR, unit)
        }.time

        return DScopeInstance(type, forwardedDate)
    }

    override fun subtract(unit: Int): DScopeInstance {
        val previousDate = (truncatedDate.clone() as Calendar).apply {
            add(Calendar.YEAR, -unit)
        }.time

        return DScopeInstance(type, previousDate)
    }

    override fun getPosition(startDate: Date): Int =
        ChronoUnit.YEARS.between(
            truncatedDate.toInstant(),
            truncate(startDate).toInstant()
        ).toInt()

    override fun truncate(dateToTruncate: Date): Calendar =
        Calendar.getInstance().apply {
            time = dateToTruncate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, -get(Calendar.DAY_OF_YEAR) + 1)
        }
}