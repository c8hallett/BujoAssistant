package com.hallett.domain.coroutines

import kotlinx.coroutines.CoroutineDispatcher

data class DispatchersWrapper(
    val main: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val default: CoroutineDispatcher
)