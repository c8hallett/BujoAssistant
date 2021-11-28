package com.hallett.scopes

import java.io.Serializable
import java.time.temporal.ChronoUnit
import java.util.Date

data class Scope internal constructor(
    val type: ScopeType,
    val value: Date,
    internal val chronoUnit: ChronoUnit,
    internal val calendarField: Int
): Serializable