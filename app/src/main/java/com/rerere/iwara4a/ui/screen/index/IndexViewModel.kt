package com.rerere.iwara4a.ui.screen.index

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rerere.iwara4a.api.paging.SubscriptionsSource
import com.rerere.iwara4a.event.LoginEvent
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.repo.UserRepo
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.util.registerListener
import com.rerere.iwara4a.util.unregisterListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager,
) : ViewModel() {
    var self by mutableStateOf(Self.GUEST)
    var email by mutableStateOf("")
    var loadingSelf by mutableStateOf(false)

    val subscriptionPager = Pager(
        config = PagingConfig(
            pageSize = 32,
            initialLoadSize = 32
        )
    ){
        SubscriptionsSource(
            sessionManager,
            mediaRepo
        )
    }.flow

    init {
        registerListener()
        refreshSelf()
    }

    override fun onCleared() {
        unregisterListener()
    }

    private fun refreshSelf() = viewModelScope.launch {
        loadingSelf = true
        email = sharedPreferencesOf("session").getString("username","请先登录你的账号吧")!!
        val response = userRepo.getSelf(sessionManager.session)
        if (response.isSuccess()) {
            self = response.read()
        }
        loadingSelf = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogin(loginEvent: LoginEvent) {
        refreshSelf()
    }
}