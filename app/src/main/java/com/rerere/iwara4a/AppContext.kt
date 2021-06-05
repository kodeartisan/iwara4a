package com.rerere.iwara4a

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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

/**
 * 使用顶层函数直接获取 SharedPreference
 *
 * @param name SharedPreference名字
 * @return SharedPreferences实例
 */
fun sharedPreferencesOf(name: String): SharedPreferences = AppContext.instance.getSharedPreferences(name, Context.MODE_PRIVATE)