package com.rerere.iwara4a.util.okhttp

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Custom, retry N interceptors
 * Set by :addInterceptor
 */
const val MAX_RETRY = 3

class Retry : Interceptor {
    private var retryNum = 0; // If set to 3 retry, the maximum possible request 4 times (default 1 + 3 retry)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request);
        Log.i("Retry", "num:$retryNum");
        while (!response.isSuccessful && retryNum < MAX_RETRY) {
            retryNum++;
            Log.i("Retry", "num:$retryNum");
            response = chain.proceed(request);
        }
        return response;
    }
}