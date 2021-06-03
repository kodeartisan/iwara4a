package com.rerere.iwara4a.api.service

import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.model.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

/**
 * 使用Jsoup来解析出网页上的资源
 *
 * 某些资源无法通过 restful api 直接获取，因此需要
 * 通过jsoup来解析
 *
 * @author RE
 */
class IwaraParser(
    private val okHttpClient: OkHttpClient
) {
    suspend fun login(username: String, password: String): Response<Session> = withContext(Dispatchers.IO){
        Response.failed("")
    }
}