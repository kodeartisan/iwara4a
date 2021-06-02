package com.rerere.iwara4a

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppContext : Application() {
    companion object {
        lateinit var instance : Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}