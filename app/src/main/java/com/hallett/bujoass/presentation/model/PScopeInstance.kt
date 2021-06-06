package com.hallett.bujoass.presentation.model

import java.util.*

data class PScopeInstance(val type: PScope, val date: Date){
    companion object{
        val NONE = PScopeInstance(PScope.NONE, Date(0))
    }
}