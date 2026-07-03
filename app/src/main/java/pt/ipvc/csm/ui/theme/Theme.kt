package pt.ipvc.csm.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
    background = CsmBackground,
    onBackground = CsmTextPrimary,
    surface = CsmSurface,
    onSurface = CsmTextPrimary,
    surfaceVariant = CsmSurfaceFill,
    onSurfaceVariant = CsmTextSecondary,
    outline = CsmOutline,
    outlineVariant = CsmDivider,
    error = CsmError,
    onError = Color.White
)

@Composable
fun CsmTheme(content: @Composable () -> Unit) {
    val colorScheme = CsmLightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CsmTypography,
        content = content
    )
}
