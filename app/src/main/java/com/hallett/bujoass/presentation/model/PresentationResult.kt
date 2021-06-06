package com.hallett.bujoass.presentation.model

sealed class PresentationResult<out T> {
    object Loading: PresentationResult<Nothing>()
    class Error(val error: Throwable): PresentationResult<Nothing>()
    class Success<T>(val data: T): PresentationResult<T>()
}