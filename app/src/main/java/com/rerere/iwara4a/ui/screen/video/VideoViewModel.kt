package com.rerere.iwara4a.ui.screen.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.SimpleExoPlayer
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.model.session.SessionManager
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
    var exoPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(AppContext.instance).build()

    fun loadVideo(id: String){
        viewModelScope.launch {
            videoId = id
            isLoading = true



            isLoading = false
        }
    }

    override fun onCleared() {
        exoPlayer.release()
    }
}