package com.rerere.iwara4a

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager

@HiltAndroidApp
class AppContext : Application() {
    companion object {
        lateinit var instance : Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 使用ExoPlayer解码
        VideoViewManager.setConfig(VideoViewConfig.newBuilder().setPlayerFactory(ExoMediaPlayerFactory.create()).build())
    }
}

/**
 * 使用顶层函数直接获取 SharedPreference
 *
 * @param name SharedPreference名字
 * @return SharedPreferences实例
 */
fun sharedPreferencesOf(name: String): SharedPreferences = AppContext.instance.getSharedPreferences(name, Context.MODE_PRIVATE)