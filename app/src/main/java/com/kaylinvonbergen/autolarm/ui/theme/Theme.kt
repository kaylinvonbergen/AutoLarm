
package com.kaylinvonbergen.autolarm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AutoLarmColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Background,

    secondary = SecondaryText,
    onSecondary = Background,

    background = Background,
    onBackground = PrimaryText,

    surface = NavigationBackground,
    onSurface = PrimaryText,

    surfaceVariant = CardBackground,
    onSurfaceVariant = PrimaryText,

    outline = BorderColor,
    outlineVariant = BorderColor.copy(alpha = 0.5f),

    error = Color(0xFFFF6B6B),
    onError = Color.White
)

@Composable
fun AutoLarmTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AutoLarmColorScheme,
        typography = Typography,
        content = content
    )
}
