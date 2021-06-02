package com.rerere.iwara4a

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    val userConfig = MutableLiveData(UserConfig("", ""))
    init {
        loadUserConfig()
    }

    private fun loadUserConfig(){
        viewModelScope.launch {
            val config = withContext(Dispatchers.IO){
                val sharedPreferences = AppContext.instance.getSharedPreferences("config", Context.MODE_PRIVATE)
                sharedPreferences.let {
                    val username = it.getString("loginUser", "")
                    val password = it.getString("loginPassword", "")

                    println("加载完成: $username")

                    UserConfig(username!!, password!!)
                }
            }
            userConfig.value = config
        }
    }

    fun saveUserConfig(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val sharedPreferences = AppContext.instance.getSharedPreferences("config", Context.MODE_PRIVATE)
                sharedPreferences.edit(false){
                    putString("loginUser", userConfig.value?.loginName)
                    putString("loginPassword", userConfig.value?.loginPassword)
                }
                println("保存成功 -> ${sharedPreferences.getString("loginUser","?")} / ${userConfig.value?.loginName}")
            }
        }
    }
}