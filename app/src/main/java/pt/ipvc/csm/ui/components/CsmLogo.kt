package pt.ipvc.csm.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmBlue

/**
 * The app logo (Campus Care): a location pin (rounded teardrop) with a heart-shaped hole in its
 * head, drawn with shapes so it scales crisply at any size. [holeColor] should match the
 * background the logo sits on so the heart reads as a cut-out.
 */
@Composable
fun CsmLogo(
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    color: Color = CsmBlue,
    holeColor: Color = CsmTheme.colors.surface
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(size)
                .rotate(45f)
                .clip(RoundedCornerShape(percent = 50).copy(bottomStart = CornerSize(size * 0.16f)))
                .background(color)
        )
        Canvas(
            Modifier
                .offset(y = -(size * 0.07f))
                .size(size * 0.46f)
        ) {
            drawPath(heartPath(this.size.width, this.size.height), color = holeColor)
        }
    }
}

/** A symmetric heart filling a [w] x [h] box, cusp at the top and tip at the bottom. */
private fun heartPath(w: Float, h: Float): Path = Path().apply {
    val cx = w / 2f
    moveTo(cx, h * 0.30f)
    cubicTo(w * 0.33f, h * 0.02f, w * 0.00f, h * 0.22f, w * 0.06f, h * 0.46f)
    cubicTo(w * 0.12f, h * 0.66f, w * 0.34f, h * 0.80f, cx, h * 0.97f)
    cubicTo(w * 0.66f, h * 0.80f, w * 0.88f, h * 0.66f, w * 0.94f, h * 0.46f)
    cubicTo(w * 1.00f, h * 0.22f, w * 0.67f, h * 0.02f, cx, h * 0.30f)
    close()
}
