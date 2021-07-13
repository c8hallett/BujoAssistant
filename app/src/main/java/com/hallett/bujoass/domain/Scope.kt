package com.hallett.bujoass.domain

import java.io.Serializable
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Calendar

sealed class Scope(val value: Date): Serializable{
    abstract val chronoUnit: ChronoUnit
    abstract fun add(unit: Int): Scope
    abstract fun getCurrent(): Scope

    fun subtract(unit: Int): Scope = add(-unit)
    fun next(): Scope = add(1)
    fun previous(): Scope = add(-1)
    fun isCurrent(): Boolean {
        return getCurrent() == this
    }
    fun getPosition(startDate: Date): Int =
        chronoUnit.between(
            value.toInstant(),
            startDate.toInstant()
        ).toInt()

    companion object {
        private fun truncateDate(date: Date, modify: (Calendar.() -> Unit)? = null): Date = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            modify?.invoke(this)
        }.time

        fun fromString(value: String): Scope {
            val splitValues = value.split(".")
            if(splitValues.size != 2) throw IllegalArgumentException("Could not parse \"$value\" into Scope, invalid format.")
            val date = try {
                Date(splitValues[2].toLong())
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("\"$value\" does not contain valid date", e)
            }
            return when(splitValues.first()) {
                Day::class.java.simpleName -> Day(date)
                Week::class.java.simpleName -> Week(date)
                Month::class.java.simpleName -> Month(date)
                Year::class.java.simpleName -> Year(date)
                else -> throw IllegalArgumentException("\"$value\" does not contain valid scope type \"${splitValues.first()}\"")
            }
        }
    }

    override fun toString(): String {
        return "${this::class.java.simpleName}.${value.time}"
    }

    class Day(value: Date = Date()): Scope(truncateDate(value)) {
        override val chronoUnit: ChronoUnit = ChronoUnit.DAYS

        override fun add(unit: Int): Day {
            val forwardedDate = Calendar.getInstance().apply {
                time = value
                add(Calendar.DAY_OF_YEAR, unit)
            }.time
            return Day(forwardedDate)
        }

        override fun getCurrent(): Scope = Day((Date()))
    }

    class Week(value: Date = Date()): Scope(truncateDate(value) {
        add(Calendar.DAY_OF_WEEK, 1 - get(Calendar.DAY_OF_WEEK))
    }) {
        override val chronoUnit: ChronoUnit = ChronoUnit.WEEKS

        override fun add(unit: Int): Day {
            val forwardedDate = Calendar.getInstance().apply {
                time = value
                add(Calendar.WEEK_OF_YEAR, unit)
            }.time
            return Day(forwardedDate)
        }

        override fun getCurrent(): Scope = Week(Date())
    }

    class Month(value: Date = Date()): Scope(truncateDate(value) {
        add(Calendar.DAY_OF_MONTH, 1 - get(Calendar.DAY_OF_MONTH))
    }) {
        override val chronoUnit: ChronoUnit = ChronoUnit.MONTHS

        override fun add(unit: Int): Day {
            val forwardedDate = Calendar.getInstance().apply {
                time = value
                add(Calendar.MONTH, unit)
            }.time
            return Day(forwardedDate)
        }

        override fun getCurrent(): Scope = Month(Date())
    }

    class Year(value: Date = Date()): Scope(truncateDate(value) {
        add(Calendar.DAY_OF_YEAR, 1 - get(Calendar.DAY_OF_YEAR))
    }) {
        override val chronoUnit: ChronoUnit = ChronoUnit.MONTHS

        override fun add(unit: Int): Day {
            val forwardedDate = Calendar.getInstance().apply {
                time = value
                add(Calendar.YEAR, unit)
            }.time
            return Day(forwardedDate)
        }

        override fun getCurrent(): Scope = Year(Date())
    }
}
