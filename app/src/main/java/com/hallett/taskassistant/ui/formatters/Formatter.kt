package com.hallett.taskassistant.ui.formatters

interface Formatter<I, O> {
    fun format(input: I): O

    companion object {
        const val EXTRA_PADDING = "extra_padding"
        const val OFFSET_LABEL = "offset_label"
        const val SIMPLE_DATE = "simple_date"
        const val SIMPLE_LABEL = "simple_label"
    }
}