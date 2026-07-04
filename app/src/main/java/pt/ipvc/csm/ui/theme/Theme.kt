package pt.ipvc.csm.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CsmLightColors = lightColorScheme(
    primary = CsmBlue,
    onPrimary = Color.White,
    primaryContainer = CsmBlueContainer,
    onPrimaryContainer = CsmBlueDark,
    secondary = CsmBlueDark,
    onSecondary = Color.White,
    background = LightCsmColors.background,
    onBackground = LightCsmColors.textPrimary,
    surface = LightCsmColors.surface,
    onSurface = LightCsmColors.textPrimary,
    surfaceVariant = LightCsmColors.surfaceFill,
    onSurfaceVariant = LightCsmColors.textSecondary,
    outline = LightCsmColors.outline,
    outlineVariant = LightCsmColors.divider,
    error = CsmError,
    onError = Color.White
)

private val CsmDarkColors = darkColorScheme(
    primary = CsmBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF10305F),
    onPrimaryContainer = CsmBlueContainer,
    secondary = CsmBlueContainer,
    onSecondary = CsmBlueDark,
    background = DarkCsmColors.background,
    onBackground = DarkCsmColors.textPrimary,
    surface = DarkCsmColors.surface,
    onSurface = DarkCsmColors.textPrimary,
    surfaceVariant = DarkCsmColors.surfaceFill,
    onSurfaceVariant = DarkCsmColors.textSecondary,
    outline = DarkCsmColors.outline,
    outlineVariant = DarkCsmColors.divider,
    error = CsmError,
    onError = Color.White
)

/** Access the app's semantic colors from any composable: `CsmTheme.colors.textPrimary`. */
object CsmTheme {
    val colors: CsmColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalCsmColors.current
}

@Composable
fun CsmTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val csmColors = if (darkTheme) DarkCsmColors else LightCsmColors
    val colorScheme = if (darkTheme) CsmDarkColors else CsmLightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalCsmColors provides csmColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CsmTypography,
            content = content
        )
    }
}
