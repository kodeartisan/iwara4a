package com.rerere.iwara4a.ui.screen.splash

import androidx.lifecycle.ViewModel
import com.rerere.iwara4a.model.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager
): ViewModel() {
    fun isLogin() = sessionManager.session.key.isNotEmpty()
}