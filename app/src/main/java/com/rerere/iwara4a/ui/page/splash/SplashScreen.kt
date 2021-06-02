package com.rerere.iwara4a.ui.page.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){
    val systemUiController = rememberSystemUiController()
    val darkMode = isSystemInDarkTheme()
    LaunchedEffect(Unit){
        delay(1500L)
        navController.navigate("index")
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.primarySurface), contentAlignment = Alignment.Center){
        Text(text = "IWARA")
    }
}