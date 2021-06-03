package com.rerere.iwara4a.model

import okhttp3.Cookie

data class Session(
    private var key: String,
    private var value: String
){
    fun toCookie() = Cookie.Builder()
        .name(key)
        .value(value)
        .domain("iwara.tv")
        .build()
}