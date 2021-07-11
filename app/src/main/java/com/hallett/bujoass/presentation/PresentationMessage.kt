package com.hallett.bujoass.presentation

sealed class PresentationMessage(val message: String) {
    class Success(message: String): PresentationMessage(message)
    class Error(message: String): PresentationMessage(message)
    class Info(message: String): PresentationMessage(message)
}