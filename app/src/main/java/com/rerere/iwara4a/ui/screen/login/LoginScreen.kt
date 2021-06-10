package com.rerere.iwara4a.ui.screen.login

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.vanpra.composematerialdialogs.*

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
            Content(loginViewModel, navController)
        }
    }
}

@Composable
private fun Content(loginViewModel: LoginViewModel, navController: NavController) {
    val context = LocalContext.current
    var showPassword by remember {
        mutableStateOf(false)
    }

    // 登录进度对话框
    val progressDialog = remember {
        MaterialDialog(onCloseRequest = {})
    }
    progressDialog.build {
        iconTitle(
            text = "登录中",
            icon = { CircularProgressIndicator(Modifier.size(30.dp)) }
        )
        message("请稍等片刻")
    }
    // 登录失败
    val failedDialog = remember {
        MaterialDialog()
    }
    failedDialog.build {
        title("登录失败")
        message("请检查你的用户名和密码是否正确，如果确定准确，请再次重试登录")
        message("错误内容: ${loginViewModel.errorContent}")
        message("(别忘记挂梯子！)")
        buttons {
            button("好的") {
                failedDialog.hide()
            }
        }
    }

    // 内容
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
            value = loginViewModel.userName,
            onValueChange = { loginViewModel.userName = it },
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
            value = loginViewModel.password,
            onValueChange = { loginViewModel.password = it },
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
                if (loginViewModel.userName.isBlank() || loginViewModel.password.isBlank()) {
                    Toast.makeText(context, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                progressDialog.show()
                loginViewModel.login {
                    // 处理结果
                    if (it) {
                        // 登录成功
                        Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                        navController.navigate("index"){
                            popUpTo("login"){
                                inclusive = true
                            }
                        }
                    } else {
                        // 登录失败
                        failedDialog.show()
                    }
                    progressDialog.hide()
                }
            }
        ) {
            Text(text = "登录")
        }

        // Spacer
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )

        Row {
            // Register
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://ecchi.iwara.tv/user/register")
                    )
                    context.startActivity(intent)
                }
            ) {
                Text(text = "注册账号")
            }
        }
    }
}

@Composable
private fun TopBar(navController: NavController) {
    FullScreenTopBar(
        title = {
            Text(text = "登录账号")
        }
    )
}