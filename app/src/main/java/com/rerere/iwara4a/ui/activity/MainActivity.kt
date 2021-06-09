package com.rerere.iwara4a.ui.activity

import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.accompanist.coil.LocalImageLoader
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rerere.iwara4a.ui.local.LocalScreenOrientation
import com.rerere.iwara4a.ui.screen.image.ImageScreen
import com.rerere.iwara4a.ui.screen.index.IndexScreen
import com.rerere.iwara4a.ui.screen.login.LoginScreen
import com.rerere.iwara4a.ui.screen.splash.SplashScreen
import com.rerere.iwara4a.ui.screen.user.UserScreen
import com.rerere.iwara4a.ui.screen.video.VideoScreen
import com.rerere.iwara4a.ui.theme.Iwara4aTheme
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var okHttpClient: OkHttpClient
    var screenOrientation by mutableStateOf(Configuration.ORIENTATION_PORTRAIT)

    @RequiresApi(Build.VERSION_CODES.R)
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

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
            CompositionLocalProvider(LocalImageLoader provides imageLoader, LocalScreenOrientation provides screenOrientation) {
                ComposeContent()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        screenOrientation = newConfig.orientation
    }

    @RequiresApi(Build.VERSION_CODES.R)
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
                    systemUiController.setStatusBarColor(Color.Transparent, false)
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

                    composable("video/{videoId}", arguments = listOf(
                        navArgument("videoId"){
                            type = NavType.StringType
                        }
                    )){
                        VideoScreen(navController, it.arguments?.getString("videoId")!!)
                    }

                    composable("image/{imageId}", arguments = listOf(
                        navArgument("imageId"){
                            type = NavType.StringType
                        }
                    )){
                        ImageScreen(navController, it.arguments?.getString("imageId")!!)
                    }

                    composable("user/{userId}", arguments = listOf(
                        navArgument("userId"){
                            type = NavType.StringType
                        }
                    )){
                        UserScreen(navController, it.arguments?.getString("userId")!!)
                    }
                }
            }
        }
    }
}