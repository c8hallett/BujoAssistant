package com.hallett.bujoass.presentation.format

import android.annotation.SuppressLint
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
class MediumDateFormatter: Formatter<PScopeInstance> {
    override fun format(input: PScopeInstance): String {
        return when(input.type){
            PScope.NONE -> "Sometime"
            PScope.DAY -> formatDayString(input.date)
            PScope.WEEK -> formatWeekString(input.date)
            PScope.MONTH -> formatMonthString(input.date)
            PScope.YEAR -> formatYearString(input.date)
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