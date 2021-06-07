package com.hallett.bujoass.presentation

import android.view.View

fun View.setVis(isVisible: Boolean?) {
    visibility = when(isVisible){
        true -> View.VISIBLE
        false -> View.INVISIBLE
        null -> View.GONE
    }
}