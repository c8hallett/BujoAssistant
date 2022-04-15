package com.hallett.taskassistant.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> CoroutineScope.debounce(
    debounceLength: Long = 500L,
    block: (T) -> Unit
): (T) -> Unit {
    var delayedJob: Job? = null
    return { param: T ->
        delayedJob?.cancel()
        delayedJob = launch {
            delay(debounceLength)
            block(param)
        }
    }
}
