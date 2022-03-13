package com.hallett.scopes.model

import java.io.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Scope internal constructor(
    val type: ScopeType,
    val value: LocalDate,
    internal val chronoUnit: ChronoUnit,
) : Serializable, Comparable<Scope> {
    override fun compareTo(other: Scope): Int = chronoUnit.between(value, other.value).toInt()
}