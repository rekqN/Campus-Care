package pt.ipvc.csm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.CsmSurface

/**
 * The app logo: a location pin (rounded teardrop with a hole), drawn with shapes so it scales
 * crisply at any size. [holeColor] should match the background the logo sits on.
 */
@Composable
fun CsmLogo(
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    color: Color = CsmBlue,
    holeColor: Color = CsmSurface
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(size)
                .rotate(45f)
                .clip(RoundedCornerShape(percent = 50).copy(bottomStart = CornerSize(size * 0.16f)))
                .background(color)
        )
        Box(
            Modifier
                .offset(y = -(size * 0.07f))
                .size(size * 0.42f)
                .clip(CircleShape)
                .background(holeColor)
        )
    }
}
