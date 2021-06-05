package com.hallett.bujoass

import android.app.Application
import timber.log.Timber

class BujoAssApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}