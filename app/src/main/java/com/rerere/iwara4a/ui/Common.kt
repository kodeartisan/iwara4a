package com.rerere.iwara4a.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.accompanist.coil.CoilImage



@Composable
fun RoundedImage(modifier: Modifier, imageLink : String){
    Box(modifier = modifier.clip(CircleShape).background(Color.LightGray)){
        CoilImage(data = imageLink, contentDescription = "rounded image", fadeIn = true)
    }
}