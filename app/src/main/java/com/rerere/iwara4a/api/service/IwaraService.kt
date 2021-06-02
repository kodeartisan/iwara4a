package com.rerere.iwara4a.api.service

import retrofit2.http.POST

interface IwaraService {
    @POST("video/{videoId}")
    suspend fun getVideoInfo(videoId: String): String
}