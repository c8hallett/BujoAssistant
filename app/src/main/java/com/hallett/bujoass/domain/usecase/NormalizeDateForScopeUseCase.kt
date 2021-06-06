package com.hallett.bujoass.domain.usecase

import com.hallett.bujoass.domain.model.DScope
import java.util.*

class NormalizeDateForScopeUseCase: INormalizeDateForScopeUseCase {
    override fun execute(scope: DScope, date: Date): Date {
        val shiftedDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            when(scope){
                DScope.DAY -> {}
                DScope.WEEK -> {
                    // get day of week and shift so that value is 0-6
                    // 6 representing saturday
                    val dayOfWeek = get(Calendar.DAY_OF_WEEK) - 1
                    // roll back that many days to truncate date to beginning of week
                    add(Calendar.DAY_OF_WEEK, -dayOfWeek)
                }
                DScope.MONTH -> {
                    // get date in month and shift so that value is zero-indexed
                    val dayInMonth = get(Calendar.DAY_OF_MONTH) - 1
                    // roll back that many days to truncate date to beginning of month
                    add(Calendar.DAY_OF_MONTH, -dayInMonth)
                }
                DScope.YEAR -> {
                    // get day in year and shift so that value is zero-indexed
                    val dayInYear = get(Calendar.DAY_OF_YEAR) - 1
                    // roll back that many days to truncate date to beginning of year
                    add(Calendar.DAY_OF_YEAR, -dayInYear)
                }
            }
        }
        return shiftedDate.time
    }

}