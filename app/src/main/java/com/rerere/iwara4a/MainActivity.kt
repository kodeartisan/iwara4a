package com.rerere.iwara4a

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rerere.iwara4a.ui.screen.index.IndexScreen
import com.rerere.iwara4a.ui.screen.login.LoginScreen
import com.rerere.iwara4a.ui.screen.splash.SplashScreen
import com.rerere.iwara4a.ui.theme.Iwara4aTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProvideWindowInsets {
                Iwara4aTheme {
                    val navController = rememberNavController()
                    val systemUiController = rememberSystemUiController()
                    val primaryColor = MaterialTheme.colors.primarySurface

                    // set ui color
                    SideEffect {
                        systemUiController.setNavigationBarColor(primaryColor)
                        systemUiController.setStatusBarColor(primaryColor, false)
                    }

                    NavHost(modifier = Modifier.fillMaxSize(), navController = navController, startDestination = "splash") {
                        composable("splash"){
                            SplashScreen(navController)
                        }

                        composable("index") {
                            IndexScreen(navController)
                        }

                        composable("login") {
                            LoginScreen(navController)
                        }
                    }
                }
            }
        }
    }
}