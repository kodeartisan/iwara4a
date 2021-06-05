package com.rerere.iwara4a.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.Gray,
    primaryVariant = Color.DarkGray,
    secondary = Color.LightGray,
    secondaryVariant = Color.Black
)

private val LightColorPalette = lightColors(
    primary = TEAL300,
    primaryVariant = TEAL700,
    secondary = Color(0xFF64FFDA),
    secondaryVariant = Color(0xFF1DE9B6),
    onSurface = TEAL300
)

@Composable
fun Iwara4aTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}