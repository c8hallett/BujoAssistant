package com.hallett.taskassistant.ui.formatters

interface IFormatter<I, O> {
    fun format(input: I): O
}