package com.erishan.traceback

import android.app.Application
import com.erishan.traceback.core.di.AppContainer

class TracebackApp: Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}