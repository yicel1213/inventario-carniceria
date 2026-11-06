package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ButcheryColorScheme = darkColorScheme(
    primary = ButcheryRedPrimary,
    onPrimary = TextPrimary,
    primaryContainer = ButcheryRedDark,
    onPrimaryContainer = ButcheryRedLight,
    secondary = ColdChamberBlue,
    onSecondary = ButcheryDarkBg,
    secondaryContainer = ColdChamberBg,
    onSecondaryContainer = ColdChamberBlue,
    tertiary = DisplayMuralGreen,
    onTertiary = ButcheryDarkBg,
    tertiaryContainer = DisplayMuralBg,
    onTertiaryContainer = DisplayMuralGreen,
    background = ButcheryDarkBg,
    onBackground = TextPrimary,
    surface = ButcherySurface,
    onSurface = TextPrimary,
    surfaceVariant = ButcherySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ButcheryBorder,
    error = ExpiredRed,
    onError = TextPrimary,
    errorContainer = ExpiredRedBg,
    onErrorContainer = ExpiredRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = ButcheryColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ButcheryRedHeader.toArgb()
                window.navigationBarColor = ButcheryDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
