package com.hallett.bujoass.presentation.format

import android.annotation.SuppressLint
import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.domain.ScopeType
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
class MediumDateFormatter: Formatter<Scope?> {
    override fun format(input: Scope?): String {
        return when(input?.type){
            null -> "Sometime"
            ScopeType.DAY -> formatDayString(input.value)
            ScopeType.WEEK -> formatWeekString(input.value)
            ScopeType.MONTH -> formatMonthString(input.value)
            ScopeType.YEAR -> formatYearString(input.value)
        }
    }

    private fun formatDayString(date: Date): String {
        return SimpleDateFormat("MMM dd, yyyy").format(date)
    }

    private fun formatWeekString(date: Date): String {
        return "Week #${SimpleDateFormat("w").format(date)}"
    }

    private fun formatMonthString(date: Date): String {
        return SimpleDateFormat("MMMM yyyy").format(date)
    }

    private fun formatYearString(date: Date): String {
        return SimpleDateFormat("yyyy").format(date)
    }
}