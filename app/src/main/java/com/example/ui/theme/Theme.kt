package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BentoPastelColorScheme =
  lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryCyan,
    tertiary = AiPurple,
    background = BentoCanvas,
    surface = BentoSurface,
    surfaceVariant = BentoSurfaceVariant,
    onPrimary = BentoSurface,
    onSecondary = BentoSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BentoBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = BentoPastelColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

