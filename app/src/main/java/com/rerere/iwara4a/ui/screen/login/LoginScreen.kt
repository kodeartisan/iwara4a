package com.rerere.iwara4a.ui.screen.login

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import com.rerere.iwara4a.R

@ExperimentalAnimationApi
@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            TopBar(navController)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .navigationBarsWithImePadding(),
            contentAlignment = Alignment.Center
        ) {
           Content()
        }
    }
}

@Composable
private fun Content(){
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var showPassword by remember {
        mutableStateOf(false)
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // LOGO
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.logo),
                contentDescription = null
            )
        }

        // Spacer
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
        )

        // Username
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = { username = it },
            label = {
                Text(
                    text = "用户名"
                )
            },
            singleLine = true
        )

        // Password
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = "密码"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                Crossfade(targetState = showPassword) {
                    IconButton(onClick = {
                        showPassword = !showPassword
                    }) {
                        Icon(
                            if (it) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            null
                        )
                    }
                }
            }
        )

        // Spacer
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        )

        // Login
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // TODO, HANDLE IT
            }
        ) {
            Text(text = "登录")
        }
    }
}

@Composable
private fun TopBar(navController: NavController) {
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(text = "登录账号")
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null)
            }
        }
    )
}