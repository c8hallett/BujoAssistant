package com.hallett.scopes.model

import java.io.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Scope internal constructor(
    val type: ScopeType,
    val value: LocalDate,
    internal val chronoUnit: ChronoUnit,
    internal val calendarField: Int
): Serializable