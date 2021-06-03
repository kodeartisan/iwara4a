package com.rerere.iwara4a.util.okhttp

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class UserAgentInterceptor(private val userAgent: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest: Request = chain.request()
            .newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(userAgentRequest)
    }
}