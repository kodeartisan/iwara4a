package com.rerere.iwara4a.ui.screen.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.model.user.UserData
import com.rerere.iwara4a.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo
): ViewModel(){
    var loading by mutableStateOf(false)
    var error by mutableStateOf(false)
    var userData by mutableStateOf(UserData.LOADING)

    fun load(userId: String){
        viewModelScope.launch {
            loading = true
            error = false

            val response = userRepo.getUser(sessionManager.session, userId)
            if(response.isSuccess()){
                userData = response.read()
            } else {
                error = true
            }

            loading = false
        }
    }

    fun isLoaded() = userData != UserData.LOADING
}