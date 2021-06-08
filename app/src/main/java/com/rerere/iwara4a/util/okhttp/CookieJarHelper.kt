package com.rerere.iwara4a.util.okhttp

import com.rerere.iwara4a.model.session.Session
import okhttp3.*

private val HAS_JS = Cookie.Builder()
    .name("has_js")
    .value("1")
    .domain("ecchi.iwara.tv")
    .build()

fun Request.Builder.applyCookie(session: Session) = this.header("cookie", "${session.key}=${session.value}; has_js=1")

class CookieJarHelper : CookieJar, Iterable<Cookie> {
    private var cookies = ArrayList<Cookie>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookies
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        this.cookies = ArrayList(cookies)
    }

    override fun iterator(): Iterator<Cookie> = cookies.iterator()

    fun clean() = cookies.clear()

    fun init(session: Session) {
        clean()
        if (session.isNotEmpty()) {
            cookies.add(session.toCookie())
            cookies.add(HAS_JS)
        } else {
            println("### NOT LOGIN ###")
        }
    }
}

fun OkHttpClient.getCookie() = this.cookieJar as CookieJarHelper