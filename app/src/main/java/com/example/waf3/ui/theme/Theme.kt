package com.example.waf3.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = PrimaryMedicalBlue,
    onSecondary = Color.White,
    tertiary = PrimaryLightBlue,
    background = BgIceBlue,
    onBackground = NeutralCharcoal,
    surface = NeutralWhite,
    onSurface = NeutralCharcoal,
    error = StatusError,
)

@Composable
fun Waf3Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}