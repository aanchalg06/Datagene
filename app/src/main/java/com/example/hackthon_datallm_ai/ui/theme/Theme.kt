package com.example.hackthon_datallm_ai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0062FE), // A tech blue
    secondary = Color(0xFF004DBF), // A cyan accent
    tertiary = Color(0xFFEB00FF), // A deep purple
    background = Color(0xFFE3E3FF), // A light color for the background
    surface = Color(0xFFC2D9FF), // A light grey for the surface
    primaryContainer = Color(0xFF0062FE), // A lighter blue-grey for containers
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color(0xFFFCDAFF),
    onBackground = Color.Black,
    onSurface =Color(0xFFFCDAFF),
    onPrimaryContainer=Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1C1C24), // A tech blue
    secondary = Color(0xFFFEEEEE), // A cyan accent
    tertiary = Color(0xFF5E35B1), // A deep purple
    background = Color(0xFF13131A), // A dark grey for the background
    surface = Color(0xFF1C1C24), // A blue-grey for the surface
    primaryContainer = Color(0xFF0062FE), // A lighter blue-grey for containers
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color(0xFFFCDAFF),
    onPrimaryContainer=Color.White
)

@Composable
fun Hackthon_DataLLM_AITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}