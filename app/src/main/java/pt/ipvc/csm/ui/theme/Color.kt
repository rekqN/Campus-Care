package pt.ipvc.csm.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Brand (shared by light & dark)
val CsmBlue = Color(0xFF1D5FD6)
val CsmBlueDark = Color(0xFF0B3EA8)
val CsmBlueContainer = Color(0xFFDCE6FF)

// Light structure/text values (also feed the light CsmColorScheme below)
val CsmBackground = Color(0xFFEEF1F8)
val CsmSurface = Color(0xFFFBFBFE)
val CsmSurfaceFill = Color(0xFFEEF2F8)
val CsmOutline = Color(0xFFC4C7D0)
val CsmDivider = Color(0xFFF0F2F6)
val CsmTextPrimary = Color(0xFF14161B)
val CsmTextSecondary = Color(0xFF45464F)
val CsmTextMuted = Color(0xFF5A6472)
val CsmTextTertiary = Color(0xFF7A8494)
val CsmTextFaint = Color(0xFF98A1B1)

// Status — background / foreground / dot (kept identical across themes)
val StatusSubmittedBg = Color(0xFFDCE6FF)
val StatusSubmittedFg = Color(0xFF0B3EA8)
val StatusSubmittedDot = Color(0xFF1D5FD6)

val StatusReviewBg = Color(0xFFFFE7C2)
val StatusReviewFg = Color(0xFF8A5000)
val StatusReviewDot = Color(0xFFE08A00)

val StatusDoneBg = Color(0xFFC7ECCE)
val StatusDoneFg = Color(0xFF10692E)
val StatusDoneDot = Color(0xFF1E8E3E)

val StatusRejectedBg = Color(0xFFFBD9D5)
val StatusRejectedFg = Color(0xFFA3160F)
val StatusRejectedDot = Color(0xFFD3352B)

val StatusCancelledBg = Color(0xFFE2E4EA)
val StatusCancelledFg = Color(0xFF4A4E57)
val StatusCancelledDot = Color(0xFF8A93A3)

// Priority — background / foreground / dot (kept identical across themes)
val PriorityLowBg = Color(0xFFE7EAF0)
val PriorityLowFg = Color(0xFF5A6472)
val PriorityLowDot = Color(0xFF98A1B1)

val PriorityMediumBg = Color(0xFFDCE6FF)
val PriorityMediumFg = Color(0xFF0B3EA8)
val PriorityMediumDot = Color(0xFF1D5FD6)

val PriorityHighBg = Color(0xFFFFE0C0)
val PriorityHighFg = Color(0xFF9A4A00)
val PriorityHighDot = Color(0xFFF07800)

val PriorityUrgentBg = Color(0xFFFBD9D5)
val PriorityUrgentFg = Color(0xFFA3160F)
val PriorityUrgentDot = Color(0xFFD3352B)

val CsmError = Color(0xFFD3352B)
val CsmDanger = Color(0xFFA3160F)

/**
 * The app's semantic colors that change between light and dark. Accessed in composables via
 * `CsmTheme.colors.*`. Brand and status colors stay constant, so they live as plain values above.
 */
@Immutable
data class CsmColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceFill: Color,
    val outline: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textTertiary: Color,
    val textFaint: Color
)

val LightCsmColors = CsmColorScheme(
    background = CsmBackground,
    surface = Color.White,
    surfaceFill = CsmSurfaceFill,
    outline = CsmOutline,
    divider = CsmDivider,
    textPrimary = CsmTextPrimary,
    textSecondary = CsmTextSecondary,
    textMuted = CsmTextMuted,
    textTertiary = CsmTextTertiary,
    textFaint = CsmTextFaint
)

val DarkCsmColors = CsmColorScheme(
    background = Color(0xFF0E1116),
    surface = Color(0xFF1A1F27),
    surfaceFill = Color(0xFF262C36),
    outline = Color(0xFF3A414D),
    divider = Color(0xFF2A2F39),
    textPrimary = Color(0xFFECEEF3),
    textSecondary = Color(0xFFC4CAD4),
    textMuted = Color(0xFFAAB2BF),
    textTertiary = Color(0xFF8A93A1),
    textFaint = Color(0xFF6C7482)
)

val LocalCsmColors = staticCompositionLocalOf { LightCsmColors }
