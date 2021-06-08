package com.rerere.iwara4a.ui.screen.image

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.model.detail.image.ImageDetail
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo
): ViewModel() {
    var imageDetail by mutableStateOf(ImageDetail.LOADING)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf(false)

    fun load(imageId: String) = viewModelScope.launch {
        isLoading = true
        error = false

        val response = mediaRepo.getImageDetail(sessionManager.session, imageId)
        if(response.isSuccess()){
            imageDetail = response.read()
        }else {
            error = true
        }

        isLoading = false
    }
}