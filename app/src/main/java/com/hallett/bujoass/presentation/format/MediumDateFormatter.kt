package com.hallett.bujoass.presentation.format

import android.annotation.SuppressLint
import com.hallett.bujoass.domain.Scope
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
class MediumDateFormatter: Formatter<Scope?> {
    override fun format(input: Scope?): String {
        return when(input){
            null -> "Sometime"
            is Scope.Day -> formatDayString(input.value)
            is Scope.Week -> formatWeekString(input.value)
            is Scope.Month -> formatMonthString(input.value)
            is Scope.Year -> formatYearString(input.value)
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