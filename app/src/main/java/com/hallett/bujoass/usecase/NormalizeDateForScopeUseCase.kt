package com.hallett.bujoass.usecase

import com.hallett.bujoass.domain.DomainScope
import java.util.*

class NormalizeDateForScopeUseCase: INormalizeDateForScopeUseCase {
    override fun execute(scope: DomainScope, date: Date): Date {
        val shiftedDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)

            when(scope){
                DomainScope.DAY -> {}
                DomainScope.WEEK -> {
                    // get day of week and shift so that value is 0-6
                    // 6 representing saturday
                    val dayOfWeek = get(Calendar.DAY_OF_WEEK) - 1
                    // roll back that many days to truncate date to beginning of week
                    add(Calendar.DAY_OF_WEEK, -dayOfWeek)
                }
                DomainScope.MONTH -> {
                    // get date in month and shift so that value is zero-indexed
                    val dayInMonth = get(Calendar.DAY_OF_MONTH) - 1
                    // roll back that many days to truncate date to beginning of month
                    add(Calendar.DAY_OF_MONTH, -dayInMonth)
                }
                DomainScope.YEAR -> {
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