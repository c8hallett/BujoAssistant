package com.hallett.bujoass.presentation

import java.lang.ref.WeakReference

fun <T> WeakReference<T>.require(message: String = "Missing required object: ${this::class.java}"): T {
    return get() ?: throw IllegalStateException(message)
}