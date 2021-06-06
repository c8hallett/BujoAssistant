package com.hallett.bujoass.domain.usecase.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}