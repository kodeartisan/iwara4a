package com.rerere.iwara4a.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.event.LoginEvent
import com.rerere.iwara4a.repo.UserRepo
import com.rerere.iwara4a.util.postEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UserRepo
): ViewModel() {
    var isLoginState by mutableStateOf(false)
    var errorContent by mutableStateOf("")

    fun login(userName: String, password: String, result: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            isLoginState = true

            val response = userRepo.login(userName, password)

            // call event
            if(response.isSuccess()){
                postEvent(LoginEvent(response.read()))
            }else {
                errorContent = response.errorMessage()
            }
            // call back
            result(response.isSuccess())

            isLoginState = false
        }
    }
}