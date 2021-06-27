package com.hallett.bujoass.presentation

import android.view.View

fun View.setVis(isVisible: Boolean?) {
    visibility = when(isVisible){
        true -> View.VISIBLE
        false -> View.INVISIBLE
        null -> View.GONE
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}