package com.rerere.iwara4a.repo

import com.rerere.iwara4a.api.IwaraApi
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.model.Session
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {
    suspend fun login(username: String, password: String): Response<Session> = iwaraApi.login(username, password)
}