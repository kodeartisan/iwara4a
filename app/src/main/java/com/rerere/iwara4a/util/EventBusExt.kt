package com.rerere.iwara4a.util

import org.greenrobot.eventbus.EventBus

/**
 * 提供快速全局访问eventBus的实例
 */
val eventBus: EventBus = EventBus.getDefault()

/**
 * 快速触发一个事件
 */
fun <T> postEvent(event: T) = eventBus.post(event)

/**
 * 快速注册监听器
 */
fun <T> T.registerListener() = eventBus.register(this)

/**
 * 快速注销监听器
 */
fun <T> T.unregisterListener() = eventBus.unregister(this)