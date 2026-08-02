package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ForestGreen,
    secondary = GoldenYellow,
    tertiary = LightGold
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ForestGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = LightForestGreen,
    onPrimaryContainer = DarkForestGreen,
    secondary = GoldenYellow,
    onSecondary = DarkGrayText,
    secondaryContainer = LightGold,
    onSecondaryContainer = DarkGold,
    background = androidx.compose.ui.graphics.Color(0xFFF9FAF8),
    surface = androidx.compose.ui.graphics.Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color so brand forest green and golden yellow are always used
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
