package com.hallett.logging

import java.util.logging.Level
import java.util.logging.Logger

fun Any.logE(message: String) {
    logger.log(Level.SEVERE, message)
}

fun Any.logE(message: String, throwable: Throwable) {
    logger.log(Level.SEVERE, message, throwable)
}

fun Any.logW(message: String, throwable: Throwable?) {
    logger.log(Level.WARNING, message, throwable)
}

fun Any.logI(message: String) {
    logger.log(Level.INFO, message)
}

fun Any.logD(message: String) {
    logger.log(Level.FINE, message)
}

fun Any.logV(message: String) {
    logger.log(Level.FINEST, message)
}

fun logE(tag: String, message: String) {
    Logger.getLogger(tag).log(Level.SEVERE, message)
}

fun logE(tag: String, message: String, throwable: Throwable) {
    Logger.getLogger(tag).log(Level.SEVERE, message, throwable)
}

fun logW(tag: String, message: String, throwable: Throwable?) {
    Logger.getLogger(tag).log(Level.WARNING, message, throwable)
}

fun logI(tag: String, message: String) {
    Logger.getLogger(tag).log(Level.INFO, message)
}

fun logD(tag: String, message: String) {
    Logger.getLogger(tag).log(Level.FINE, message)
}

fun logV(tag: String, message: String) {
    Logger.getLogger(tag).log(Level.FINEST, message)
}

private val Any.logger: Logger
    get() = Logger.getLogger(this::class.java.simpleName)