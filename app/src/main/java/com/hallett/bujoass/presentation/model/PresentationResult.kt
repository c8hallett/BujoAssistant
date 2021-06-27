package com.hallett.bujoass.presentation.model

sealed class PresentationResult<out T: Any?> {
    object Loading: PresentationResult<Nothing>()
    class Error(val error: Throwable): PresentationResult<Nothing>()
    class Success<T: Any?>(val data: T): PresentationResult<T>()
}