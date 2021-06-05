package com.rerere.iwara4a.model.session

import okhttp3.Cookie

data class Session(
    var key: String,
    var value: String
){
    fun toCookie() = Cookie.Builder()
        .name(key)
        .value(value)
        .domain("iwara.tv")
        .build()

    fun isNotEmpty() = key.isNotEmpty() && value.isNotEmpty()
}