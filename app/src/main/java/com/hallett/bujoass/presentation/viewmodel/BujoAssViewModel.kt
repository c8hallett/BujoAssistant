package com.hallett.bujoass.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.hallett.bujoass.presentation.model.PresentationResult
import kotlinx.coroutines.flow.MutableSharedFlow

open class BujoAssViewModel: ViewModel() {
    suspend inline fun <T> MutableSharedFlow<PresentationResult<T>>.emitResult(operation: () -> T){
        try{
            emitSuccess(operation())
        } catch(t: Throwable){
            emitFailure(t)
        }
    }

    suspend inline fun <T> MutableSharedFlow<PresentationResult<T>>.emitResultWithLoading(operation: () -> T){
        emitLoading()
        try{
            emitSuccess(operation())
        } catch(t: Throwable){
            emitFailure(t)
        }
    }

    suspend inline fun <T> MutableSharedFlow<PresentationResult<T>>.emitLoading() {
        emit(PresentationResult.Loading)
    }
    suspend inline fun <T> MutableSharedFlow<PresentationResult<T>>.emitSuccess(value: T) {
        emit(PresentationResult.Success(value))
    }
    suspend inline fun <T> MutableSharedFlow<PresentationResult<T>>.emitFailure(error: Throwable) {
        emit(PresentationResult.Error(error))
    }
}