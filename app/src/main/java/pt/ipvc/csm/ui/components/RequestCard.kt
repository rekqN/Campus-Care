package pt.ipvc.csm.ui.components

import pt.ipvc.csm.ui.theme.CsmTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipvc.csm.data.local.RequestWithDetails
import pt.ipvc.csm.util.DateUtils

/**
 * The white request card used in every list. [showAuthor] shows the request's author (admin
 * views) instead of the category in the subtitle line.
 */
@Composable
fun RequestCard(
    item: RequestWithDetails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAuthor: Boolean = false
) {
    val request = item.request
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = CsmTheme.colors.surface,
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(iconForKey(item.categoryIcon))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = request.title,
                        color = CsmTheme.colors.textPrimary,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "#${request.id}",
                        color = CsmTheme.colors.textFaint,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                val subtitle = if (showAuthor) {
                    "${item.userName} · ${request.location}"
                } else {
                    "${item.categoryName ?: "Sem categoria"} · ${request.location}"
                }
                Text(
                    text = subtitle,
                    color = CsmTheme.colors.textMuted,
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(request.status)
                    Text(
                        text = DateUtils.formatRelative(request.createdAt),
                        color = CsmTheme.colors.textFaint,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
