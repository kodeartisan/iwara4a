package com.rerere.iwara4a.ui.screen.video

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rerere.iwara4a.ui.public.ExoPlayer
import com.rerere.iwara4a.ui.public.FullScreenTopBar

@Composable
fun VideoScreen(
    navController: NavController,
    videoId: String,
    videoViewModel: VideoViewModel = hiltViewModel()
) {
    val config = LocalConfiguration.current.orientation
    val videoLink =
        "https://yuuki.iwara.tv/file.php?expire=1622970832&hash=9bb2a8bf01cc06da4986729cc7a6af68591bb98d&file=2021%2F04%2F30%2F1619767159_9jqbJSe8qHZMo8Ja_Source.mp4&op=dl&r=0"

    Scaffold(
        topBar = {
            if (config == Configuration.ORIENTATION_PORTRAIT) {
                FullScreenTopBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    title = {
                        Text(text = "播放视频")
                    }
                )
            }
        }
    ) {
        Column {
            ExoPlayer(
                modifier = if (config == Configuration.ORIENTATION_PORTRAIT)
                    Modifier
                        .wrapContentHeight()
                        .requiredHeightIn(max = 250.dp)
                else
                    Modifier.fillMaxSize(),
                videoLink = videoLink,
                exoPlayer = videoViewModel.exoPlayer
            )
        }
    }
}