package com.rerere.iwara4a.ui.screen.index

import androidx.lifecycle.ViewModel
import com.rerere.iwara4a.event.LoginEvent
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.UserRepo
import com.rerere.iwara4a.util.registerListener
import com.rerere.iwara4a.util.unregisterListener
import dagger.hilt.android.lifecycle.HiltViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager,
) : ViewModel() {
    init {
        registerListener()
    }
    override fun onCleared() {
        unregisterListener()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogin(loginEvent: LoginEvent){
        println("LOGIN: ${loginEvent.session}")
        // TODO: Refresh UI
    }
}