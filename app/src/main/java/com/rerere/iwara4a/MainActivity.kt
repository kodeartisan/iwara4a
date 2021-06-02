package com.rerere.iwara4a

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rerere.iwara4a.ui.page.index.IndexPage
import com.rerere.iwara4a.ui.page.login.LoginPage
import com.rerere.iwara4a.ui.theme.Iwara4aTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                        println("SET UI COLOR")
                        systemUiController.setNavigationBarColor(primaryColor)
                        systemUiController.setStatusBarColor(primaryColor, false)
                    }

                    NavHost(navController, "index") {
                        composable("index") {
                            IndexPage(navController)
                        }

                        composable("login") {
                            LoginPage()
                        }
                    }
                }
            }
        }
    }
}