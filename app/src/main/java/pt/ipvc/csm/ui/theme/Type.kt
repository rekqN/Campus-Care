package pt.ipvc.csm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Roboto is the default Android system font, so FontFamily.Default already renders Roboto,
// matching the design without bundling font files.
private val Roboto = FontFamily.Default

val CsmTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal, fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal, fontSize = 13.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 14.sp
    )
)
