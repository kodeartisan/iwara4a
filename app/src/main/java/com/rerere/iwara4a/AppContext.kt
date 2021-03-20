package com.rerere.iwara4a

import android.app.Application

class AppContext : Application() {
    companion object {
        lateinit var instance : Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}