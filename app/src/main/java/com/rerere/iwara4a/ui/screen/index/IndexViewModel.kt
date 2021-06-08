package com.rerere.iwara4a.ui.screen.index

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.api.paging.MediaSource
import com.rerere.iwara4a.api.paging.SubscriptionsSource
import com.rerere.iwara4a.event.LoginEvent
import com.rerere.iwara4a.model.index.MediaQueryParam
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.index.SortType
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

    // Pager: 视频列表
    var videoQueryParam: MediaQueryParam by mutableStateOf(MediaQueryParam(SortType.DATE, emptyList()))
    val videoPager by lazy {
        Pager(config = PagingConfig(pageSize = 32, initialLoadSize = 32))
        {
            MediaSource(
                MediaType.VIDEO,
                mediaRepo,
                sessionManager,
                videoQueryParam
            )
        }.flow.cachedIn(viewModelScope)
    }

    // Pager: 订阅列表
    val subscriptionPager by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 32,
                initialLoadSize = 32,
                prefetchDistance = 8
            )
        ) {
            SubscriptionsSource(
                sessionManager,
                mediaRepo
            )
        }.flow.cachedIn(viewModelScope)
    }

    // 图片列表
    var imageQueryParam: MediaQueryParam by mutableStateOf(MediaQueryParam(SortType.DATE, emptyList()))
    val imagePager by lazy {
        Pager(config = PagingConfig(pageSize = 32, initialLoadSize = 32, prefetchDistance = 8))
        {
            MediaSource(
                MediaType.IMAGE,
                mediaRepo,
                sessionManager,
                imageQueryParam
            )
        }.flow.cachedIn(viewModelScope)
    }

    init {
        registerListener()
        refreshSelf()
    }

    override fun onCleared() {
        unregisterListener()
    }

    fun refreshSelf() = viewModelScope.launch {
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