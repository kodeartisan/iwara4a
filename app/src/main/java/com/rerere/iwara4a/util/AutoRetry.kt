package com.rerere.iwara4a.util

import android.util.Log
import androidx.annotation.IntRange
import com.rerere.iwara4a.api.Response

private const val TAG = "AutoRetry"

suspend fun <T> autoRetry(@IntRange(from = 2) maxRetry: Int = 5, action: suspend () -> Response<T>): Response<T> {
    repeat(maxRetry - 1) {
        Log.i(TAG, "autoRetry: try to get response: ${it + 1}/$maxRetry")
        val start = System.currentTimeMillis()
        val response = action()
        if (response.isSuccess()) {
            Log.i(TAG, "autoRetry: Successful get response (${System.currentTimeMillis() - start} ms)")
            return response
        }
    }
    Log.i(TAG, "autoRetry: try to get response: last/$maxRetry")
    return action()
}