package com.example.genrechain.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkGrey      = Color(0xFF111324)
val ContainerGrey = Color(0xFF1b1e40)
val PurpleText    = Color(0xFFBB86FC)

private val AppColors = darkColors(
    primary       = PurpleText,
    secondary     = PurpleText,
    background    = DarkGrey,
    surface       = ContainerGrey,
    onPrimary     = Color.Black,
    onSecondary   = Color.Black,
    onBackground  = PurpleText,
    onSurface     = PurpleText
)

@Composable
fun GenreChainTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors     = AppColors,
        typography = Typography(),
        shapes     = Shapes(),
        content    = content
    )
}
