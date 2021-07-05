package com.hallett.bujoass.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class PScopeInstance(val scope: PScope, val date: Date): Parcelable{
    companion object{
        val NONE = PScopeInstance(PScope.NONE, Date(0))
    }
}