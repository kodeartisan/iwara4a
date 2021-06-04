package com.rerere.iwara4a.event

import com.rerere.iwara4a.model.session.Session

/**
 * 用户登录事件
 */
data class LoginEvent(
    val session: Session
)