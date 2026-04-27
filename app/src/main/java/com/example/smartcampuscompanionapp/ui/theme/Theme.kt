package com.example.smartcampuscompanionapp.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// Admin Scheme - Light
private val AdminArgonLightScheme = lightColorScheme(
    primary = AdminArgonPrimary,
    onPrimary = Color.White,
    secondary = AdminArgonGradientEnd,
    onSecondary = Color.White,
    tertiary = AdminArgonGradientStart,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    errorContainer = AdminArgonGradientEnd,
    onErrorContainer = Color.White
)

// Admin Scheme - Dark
private val AdminArgonDarkScheme = darkColorScheme(
    primary = AdminArgonPrimary,
    onPrimary = Color.White,
    secondary = AdminArgonGradientEnd,
    onSecondary = Color.White,
    tertiary = AdminArgonGradientStart,
    background = Color(0xFF121212), // Dark Background
    surface = Color(0xFF1E1E1E),    // Dark Surface
    onBackground = Color.White,
    onSurface = Color.White,
    errorContainer = AdminArgonGradientEnd,
    onErrorContainer = Color.White
)

@Composable
fun SmartCampusCompanionAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isAdmin: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isAdmin -> if (darkTheme) AdminArgonDarkScheme else AdminArgonLightScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
