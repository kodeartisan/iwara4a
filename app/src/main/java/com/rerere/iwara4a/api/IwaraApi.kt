package com.rerere.iwara4a.api

import com.rerere.iwara4a.model.session.Session
import com.rerere.iwara4a.model.user.Self

/**
 * 提供远程资源API, 通过连接IWARA来获取数据
 */
interface IwaraApi {
    /**
     * 尝试登录Iwara
     *
     * @param username 登录用户名
     * @param password 登录密码
     * @return session cookie
     */
    suspend fun login(username: String, password: String): Response<Session>

    /**
     * 获取基础的个人信息
     *
     * @param session 登录凭据
     * @return 简短的个人信息
     */
    suspend fun getSelf(session: Session): Response<Self>
}