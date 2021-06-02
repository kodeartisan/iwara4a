package com.rerere.iwara4a

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rerere.iwara4a.ui.LocalNavController
import com.rerere.iwara4a.ui.page.index.IndexPage
import com.rerere.iwara4a.ui.page.login.LoginPage
import com.rerere.iwara4a.ui.theme.Iwara4aTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Iwara4aTheme {
                val navController = rememberNavController()
                CompositionLocalProvider(LocalNavController provides navController) {
                    NavHost(navController, "index"){
                        composable("index"){
                            IndexPage()
                        }

                        composable("login"){
                            LoginPage()
                        }
                    }
                }
            }
        }
    }
}