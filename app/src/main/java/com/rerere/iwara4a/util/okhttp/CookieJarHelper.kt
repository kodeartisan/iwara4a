package com.rerere.iwara4a.util.okhttp

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

class CookieJarHelper : CookieJar, Iterable<Cookie> {
    private var cookies = ArrayList<Cookie>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> = cookies

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        this.cookies = ArrayList(cookies)
    }

    override fun iterator(): Iterator<Cookie> = cookies.iterator()

    fun clean() = cookies.clear()
}

fun OkHttpClient.getCookie() = this.cookieJar as CookieJarHelper