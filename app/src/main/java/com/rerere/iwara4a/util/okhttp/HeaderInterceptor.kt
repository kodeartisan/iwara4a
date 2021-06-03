package com.rerere.iwara4a.util.okhttp

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor(private val name: String, private val value: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val headerRequest: Request = chain.request()
            .newBuilder()
            .addHeader(name , value)
            .build()
        return chain.proceed(headerRequest)
    }
}