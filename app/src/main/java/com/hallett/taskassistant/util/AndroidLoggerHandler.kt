package com.hallett.taskassistant.util

import android.util.Log
import com.hallett.taskassistant.BuildConfig
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.LogRecord

class AndroidLoggerHandler : Handler() {

    companion object {
        fun setup() = LogManager.getLogManager().getLogger("").run {
            handlers.forEach { removeHandler(it) }
            addHandler(AndroidLoggerHandler())
            level = Level.FINEST
        }
    }

    override fun isLoggable(record: LogRecord?): Boolean =
        super.isLoggable(record) && BuildConfig.DEBUG

    override fun close() {}

    override fun flush() {}

    override fun publish(record: LogRecord) {
        val tag = record.loggerName
        val level = getAndroidLevel(record.level)
        val message = when (val throwable = record.thrown) {
            null -> record.message
            else -> "${record.message}: ${Log.getStackTraceString(throwable)}"
        }

        try {
            Log.println(level, tag, message)
        } catch (e: RuntimeException) {
            Log.e(this.javaClass.simpleName, "Error logging message", e)
        }
    }

    private fun getAndroidLevel(level: Level): Int = when (level) {
        Level.SEVERE -> Log.ERROR
        Level.WARNING -> Log.WARN
        Level.INFO -> Log.INFO
        Level.FINE -> Log.DEBUG
        else -> Log.VERBOSE
    }
}