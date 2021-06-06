package com.hallett.bujoass.usecase.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}