package com.rerere.iwara4a

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.accompanist.coil.LocalImageLoader
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rerere.iwara4a.ui.screen.index.IndexScreen
import com.rerere.iwara4a.ui.screen.login.LoginScreen
import com.rerere.iwara4a.ui.screen.splash.SplashScreen
import com.rerere.iwara4a.ui.theme.Iwara4aTheme
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var okHttpClient: OkHttpClient

    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val imageLoader = ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .componentRegistry {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder(this@MainActivity))
                } else {
                    add(GifDecoder())
                }
            }
            .build()

        setContent {
            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                ComposeContent()
            }
        }
    }

    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    @ExperimentalPagerApi
    @Composable
    private fun ComposeContent(){
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