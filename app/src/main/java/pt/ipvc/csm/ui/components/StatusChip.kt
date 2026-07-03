package pt.ipvc.csm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.ui.theme.StatusCancelledBg
import pt.ipvc.csm.ui.theme.StatusCancelledDot
import pt.ipvc.csm.ui.theme.StatusCancelledFg
import pt.ipvc.csm.ui.theme.StatusDoneBg
import pt.ipvc.csm.ui.theme.StatusDoneDot
import pt.ipvc.csm.ui.theme.StatusDoneFg
import pt.ipvc.csm.ui.theme.StatusRejectedBg
import pt.ipvc.csm.ui.theme.StatusRejectedDot
import pt.ipvc.csm.ui.theme.StatusRejectedFg
import pt.ipvc.csm.ui.theme.StatusReviewBg
import pt.ipvc.csm.ui.theme.StatusReviewDot
import pt.ipvc.csm.ui.theme.StatusReviewFg
import pt.ipvc.csm.ui.theme.StatusSubmittedBg
import pt.ipvc.csm.ui.theme.StatusSubmittedDot
import pt.ipvc.csm.ui.theme.StatusSubmittedFg

data class StatusPalette(val bg: Color, val fg: Color, val dot: Color)

fun paletteFor(status: RequestStatus): StatusPalette = when (status) {
    RequestStatus.SUBMETIDO -> StatusPalette(StatusSubmittedBg, StatusSubmittedFg, StatusSubmittedDot)
    RequestStatus.EM_ANALISE -> StatusPalette(StatusReviewBg, StatusReviewFg, StatusReviewDot)
    RequestStatus.CONCLUIDO -> StatusPalette(StatusDoneBg, StatusDoneFg, StatusDoneDot)
    RequestStatus.REJEITADO -> StatusPalette(StatusRejectedBg, StatusRejectedFg, StatusRejectedDot)
    RequestStatus.CANCELADO -> StatusPalette(StatusCancelledBg, StatusCancelledFg, StatusCancelledDot)
}

/** The colored pill (dot + label) used everywhere a request status is shown. */
@Composable
fun StatusChip(status: RequestStatus, modifier: Modifier = Modifier) {
    val palette = paletteFor(status)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(palette.bg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(palette.dot)
        )
        Text(
            text = status.ptLabel,
            color = palette.fg,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
