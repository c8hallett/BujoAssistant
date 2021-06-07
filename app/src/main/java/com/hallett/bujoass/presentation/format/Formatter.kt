package com.hallett.bujoass.presentation.format

interface Formatter<T> {
    fun format(input: T): String
}