package com.hallett.bujoass.domain.model

enum class TaskStatus {
    INCOMPLETE,
    COMPLETE,
//    DEFERRED, // only need this state if we will store task both in scope it was deferred from and deferred to
//    RESCHEDULED, // only need this state if we will store task both in scope it was rescheduled from and rescheduled to
    CANCELLED,
}