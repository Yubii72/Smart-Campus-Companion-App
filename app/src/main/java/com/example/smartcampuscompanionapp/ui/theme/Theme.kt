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

// DEFAULT DARK COLORS
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// DEFAULT LIGHT COLORS
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// ADMIN LIGHT THEME
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

// ADMIN DARK THEME
private val AdminArgonDarkScheme = darkColorScheme(
    primary = AdminArgonPrimary,
    onPrimary = Color.White,
    secondary = AdminArgonGradientEnd,
    onSecondary = Color.White,
    tertiary = AdminArgonGradientStart,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White,
    errorContainer = AdminArgonGradientEnd,
    onErrorContainer = Color.White
)

/**
 * MAIN THEME WRAPPER
 * Handles color scheme switching based on role and system settings
 */
@Composable
fun SmartCampusCompanionAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isAdmin: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // SELECT COLOR SCHEME
    val colorScheme = when {
        // Apply Argon theme if Admin
        isAdmin -> if (darkTheme) AdminArgonDarkScheme else AdminArgonLightScheme
        
        // Android 12+ Dynamic Color support
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        
        // Default modes
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // UPDATE SYSTEM UI (Status Bar)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // APPLY MATERIAL 3 THEME
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
