package com.hallett.bujoass.domain

import java.io.Serializable
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Calendar

class Scope private constructor(
    val type: ScopeType,
    val value: Date,
    private val chronoUnit: ChronoUnit,
    private val calendarField: Int
    ): Serializable {

    fun add(unit: Int): Scope {
        val forwardedDate = Calendar.getInstance().apply {
            time = value
            add(calendarField, unit)
        }.time

        return Scope(ScopeType.DAY, forwardedDate, chronoUnit, calendarField)
    }

    fun subtract(unit: Int): Scope = add(-unit)
    fun next(): Scope = add(1)
    fun previous(): Scope = add(-1)
    fun getPosition(startDate: Date): Int =
        chronoUnit.between(
            value.toInstant(),
            startDate.toInstant()
        ).toInt()
    fun isCurrent(): Boolean = (this == getCurrent(type)) // todo: compare this to just

    override fun equals(other: Any?): Boolean = when(other){
        is Scope -> other.value == this.value && other.type == this.type
        else -> false
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    companion object {

        private fun truncateToDate(date: Date, modify: (Calendar.() -> Unit)? = null): Date = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            modify?.invoke(this)
        }.time

        fun newInstance(scopeType: ScopeType, date: Date): Scope {
            return when(scopeType) {
                ScopeType.DAY -> Scope(
                    type = ScopeType.DAY,
                    value = truncateToDate(date),
                    chronoUnit = ChronoUnit.DAYS,
                    calendarField = Calendar.DAY_OF_YEAR,
                )
                ScopeType.WEEK -> Scope(
                    type = ScopeType.WEEK,
                    value = truncateToDate(date){ add(Calendar.DAY_OF_WEEK, 1 - get(Calendar.DAY_OF_WEEK)) },
                    chronoUnit = ChronoUnit.WEEKS,
                    calendarField = Calendar.WEEK_OF_YEAR,
                )
                ScopeType.MONTH -> Scope(
                    type = ScopeType.MONTH,
                    value = truncateToDate(date) { add(Calendar.DAY_OF_MONTH, 1 - get(Calendar.DAY_OF_MONTH)) },
                    chronoUnit = ChronoUnit.MONTHS,
                    calendarField = Calendar.MONTH,
                )
                ScopeType.YEAR -> Scope(
                    type = ScopeType.YEAR,
                    value = truncateToDate(date) { add(Calendar.DAY_OF_YEAR, 1 - get(Calendar.DAY_OF_YEAR)) },
                    chronoUnit = ChronoUnit.YEARS,
                    calendarField = Calendar.YEAR,
                )
            }
        }

        fun getCurrent(scopeType: ScopeType) = newInstance(scopeType, Date())
    }
}

enum class ScopeType {
    DAY,
    WEEK,
    MONTH,
    YEAR
}