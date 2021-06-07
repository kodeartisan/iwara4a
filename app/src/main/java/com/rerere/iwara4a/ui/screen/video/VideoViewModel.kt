package com.rerere.iwara4a.ui.screen.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.model.video.VideoDetail
import com.rerere.iwara4a.repo.MediaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo
): ViewModel() {
    var videoId by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf(false)
    var videoDetail by mutableStateOf(VideoDetail.LOADING)

    fun loadVideo(id: String){
        if(videoDetail != VideoDetail.LOADING){
            return
        }

        viewModelScope.launch {
            videoId = id
            isLoading = true
            error = false

            val response = mediaRepo.getVideoDetail(sessionManager.session, id)
            if(response.isSuccess()){
                videoDetail = response.read()
            }else {
                error = true
            }

            isLoading = false
        }
    }
}