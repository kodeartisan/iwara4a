package com.rerere.iwara4a.util

import android.util.Log
import androidx.annotation.IntRange
import com.rerere.iwara4a.api.Response

private const val TAG = "AutoRetry"

/**
 * 自动重试函数, 用于iwara的服务器大概率会无响应，因此需要尝试多次才能获取到响应内容
 *
 * @param maxRetry 重试次数
 * @param action 重试体
 * @return 最终响应
 */
suspend fun <T> autoRetry(@IntRange(from = 2) maxRetry: Int = 7, action: suspend () -> Response<T>): Response<T> {
    repeat(maxRetry - 1) {
        Log.i(TAG, "autoRetry: Try to get response: ${it + 1}/$maxRetry")
        val start = System.currentTimeMillis()
        val response = action()
        if (response.isSuccess()) {
            Log.i(TAG, "autoRetry: Successful get response (${System.currentTimeMillis() - start} ms)")
            return response
        }
    }
    Log.i(TAG, "autoRetry: Try to get response: $maxRetry*/$maxRetry")
    return action()
}