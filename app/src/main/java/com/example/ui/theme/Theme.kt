package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VibrantPurplePrimaryDark,
    onPrimary = Color(0xFF381E72),
    primaryContainer = VibrantPurpleContainerDark,
    onPrimaryContainer = VibrantPurpleOnContainerDark,
    secondary = VibrantSecondaryDark,
    onSecondary = Color(0xFF332D41),
    secondaryContainer = VibrantSecondaryContainerDark,
    onSecondaryContainer = VibrantSecondaryOnContainerDark,
    tertiary = VibrantTertiaryDark,
    onTertiary = Color(0xFF492532),
    tertiaryContainer = VibrantTertiaryContainerDark,
    onTertiaryContainer = VibrantTertiaryOnContainerDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = VibrantPurpleContainer,
    onPrimaryContainer = VibrantPurpleOnContainer,
    secondary = VibrantSecondary,
    onSecondary = Color.White,
    secondaryContainer = VibrantSecondaryContainer,
    onSecondaryContainer = VibrantSecondaryOnContainer,
    tertiary = VibrantTertiary,
    onTertiary = Color.White,
    tertiaryContainer = VibrantTertiaryContainer,
    onTertiaryContainer = VibrantTertiaryOnContainer,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

@Composable
fun GradLaunchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
