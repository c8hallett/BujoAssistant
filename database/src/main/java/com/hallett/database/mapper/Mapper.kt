package com.hallett.database.mapper

interface Mapper<External, Internal> {
    fun External.toInternal(): Internal
    fun Internal.toExternal(): External
}