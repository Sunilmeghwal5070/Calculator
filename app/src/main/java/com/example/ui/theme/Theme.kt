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
    primary = Color(0xFFFF9F0A),
    secondary = Color(0xFF333333),
    tertiary = Color(0xFFA5A5A5),
    background = Color(0xFF0D0D0D),
    surface = Color(0xFF1B1B1B),
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF9F0A),
    secondary = Color(0xFF333333),
    tertiary = Color(0xFFA5A5A5)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for premium look
  dynamicColor: Boolean = false, // Disable dynamic color to maintain custom aesthetic
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
