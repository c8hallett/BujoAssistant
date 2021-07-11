package com.hallett.bujoass.presentation.ui

import androidx.lifecycle.ViewModel
import com.hallett.bujoass.presentation.PresentationMessage
import com.hallett.bujoass.presentation.model.PresentationResult
import kotlinx.coroutines.flow.FlowCollector

open class BujoAssViewModel: ViewModel() {
    suspend inline fun <T> FlowCollector<PresentationResult<T>>.emitResult(operation: () -> T){
        emitLoading()
        try{
            successMessage(operation())
        } catch(t: Throwable){
            emitFailure(t)
        }
    }

    suspend inline fun <T> FlowCollector<PresentationResult<T>>.emitLoading() {
        emit(PresentationResult.Loading)
    }
    suspend inline fun <T> FlowCollector<PresentationResult<T>>.successMessage(value: T) {
        emit(PresentationResult.Success(value))
    }
    suspend inline fun <T> FlowCollector<PresentationResult<T>>.emitFailure(error: Throwable) {
        emit(PresentationResult.Error(error))
    }

    suspend inline fun FlowCollector<PresentationMessage>.emitMessage(operation: () -> String, onFailure: (Throwable) -> String?)  {
        try {
            successMessage(operation())
        } catch(t: Throwable) {
            when(val message = onFailure(t)) {
                null -> {} // ignore
                else -> errorMessage(message)
            }
        }
    }

    suspend inline fun FlowCollector<PresentationMessage>.successMessage(message: String) {
        emit(PresentationMessage.Success(message))
    }
    suspend inline fun FlowCollector<PresentationMessage>.errorMessage(message: String) {
        emit(PresentationMessage.Error(message))
    }
    suspend inline fun FlowCollector<PresentationMessage>.infoMessage(message: String) {
        emit(PresentationMessage.Info(message))
    }
}