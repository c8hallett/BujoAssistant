package com.hallett.bujoass.presentation

import androidx.annotation.StringRes
import com.hallett.bujoass.R

enum class PresentationScope(@StringRes val displayName: Int) {
    NONE(R.string.display_scope_none),
    DAY(R.string.display_scope_day),
    WEEK(R.string.display_scope_week),
    MONTH(R.string.display_scope_month),
    YEAR(R.string.display_scope_year),
}