package com.rerere.iwara4a.ui.page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rerere.iwara4a.MainViewModel

@Composable
fun IndexPage(){
    val viewModel = viewModel<MainViewModel>()
    Scaffold {
        Card(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()) {
            Column(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .animateContentSize()) {
                val userConfig by viewModel.userConfig.observeAsState()
                var username by remember{ mutableStateOf(userConfig!!.loginName)}
                
                TextField(value = username, onValueChange = {
                    username = it
                })
                
                Spacer(modifier = Modifier.height(10.dp).fillMaxWidth())
                
                Button(onClick = {
                    userConfig!!.loginName = username
                    viewModel.userConfig.value = userConfig
                    viewModel.saveUserConfig()
                    println("保存 = ${userConfig!!.loginName}")
                }) {
                    Text(text = "登录")
                }
            }
        }
    }
}