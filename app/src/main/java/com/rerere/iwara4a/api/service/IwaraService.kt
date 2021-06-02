package com.rerere.iwara4a.api.service

import retrofit2.http.POST

/**
 * 使用Retrofit直接获取 RESTFUL API 资源
 */
interface IwaraService {
    @POST("video/{videoId}")
    suspend fun getVideoInfo(videoId: String): String
}