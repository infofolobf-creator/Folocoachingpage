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
    primary = TerracottaLight,
    secondary = GoldOcher,
    tertiary = IndigoSea,
    background = DarkBackgroundCombined,
    surface = DarkEarthy,
    primaryContainer = TerracottaDark,
    onPrimaryContainer = WarmWhite,
    secondaryContainer = IndigoSea,
    onSecondaryContainer = Color.White,
    tertiaryContainer = GoldOcher.copy(alpha = 0.2f),
    onTertiaryContainer = GoldOcher,
    surfaceVariant = DarkBackgroundCombined,
    onSurfaceVariant = WarmWhite,
    outline = TerracottaLight.copy(alpha = 0.3f),
    onPrimary = WarmWhite,
    onSecondary = DarkEarthy,
    onTertiary = WarmWhite,
    onBackground = CreamSand,
    onSurface = CreamSand
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Terracotta,
    secondary = IndigoSea,
    tertiary = GoldOcher,
    background = CreamSand,
    surface = Color.White,
    primaryContainer = SoftGrayCultural,
    onPrimaryContainer = Terracotta,
    secondaryContainer = IndigoSea,
    onSecondaryContainer = Color.White,
    tertiaryContainer = GoldOcher.copy(alpha = 0.15f),
    onTertiaryContainer = Terracotta,
    surfaceVariant = SoftGrayCultural,
    onSurfaceVariant = DarkEarthy,
    outline = Terracotta.copy(alpha = 0.15f),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = DarkEarthy,
    onBackground = DarkEarthy,
    onSurface = DarkEarthy
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is disabled by default to enforce the custom premium African styling
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
