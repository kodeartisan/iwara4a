package com.rerere.iwara4a.ui.public

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

private const val TAG = "ExoPlayerCompose"

@Composable
fun ExoPlayer(modifier: Modifier = Modifier,exoPlayer: SimpleExoPlayer, videoLink: String) {
    LaunchedEffect(videoLink) {
        if (videoLink.isNotEmpty()) {
            exoPlayer.setMediaItem(MediaItem.fromUri(videoLink))
            exoPlayer.prepare()
        }
    }

    AndroidView(modifier = modifier, factory = {
        PlayerView(it).apply {
            player = exoPlayer
        }
    })
}