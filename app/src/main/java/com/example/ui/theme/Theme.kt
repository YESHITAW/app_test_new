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

private val DarkColorScheme =
  darkColorScheme(
    primary = ElectricSky,
    onPrimary = NavyDarkBackground,
    primaryContainer = ElectricSkyContainer,
    onPrimaryContainer = Color.White,
    secondary = AmberTape,
    onSecondary = Color.Black,
    secondaryContainer = AmberTapeContainer,
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = EmeraldRecord,
    onTertiary = Color.White,
    background = NavyDarkBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    error = RoseAlarm,
    onError = Color.White
  )

private val LightColorScheme = DarkColorScheme // Focused dark mode aesthetic for answering machine HUD

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use intentional themed colors
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
