package pt.ipvc.csm.ui.screens.user

import androidx.compose.ui.res.stringResource
import pt.ipvc.csm.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipvc.csm.data.local.RequestWithDetails
import pt.ipvc.csm.data.local.UserEntity
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.ui.components.RequestCard
import pt.ipvc.csm.ui.components.UserAvatar
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.StatusDoneDot
import pt.ipvc.csm.ui.theme.StatusReviewDot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeTab(
    user: UserEntity,
    requests: List<RequestWithDetails>,
    unreadCount: Int,
    onNewRequest: () -> Unit,
    onOpenRequest: (Long) -> Unit,
    onSeeAllRequests: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    val submitted = requests.count { it.request.status == RequestStatus.SUBMETIDO }
    val underReview = requests.count { it.request.status == RequestStatus.EM_ANALISE }
    val completed = requests.count { it.request.status == RequestStatus.CONCLUIDO }
    val recent = requests.take(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.greeting), color = CsmTheme.colors.textMuted, fontSize = 12.sp)
                Text(user.name, color = CsmTheme.colors.textPrimary, fontSize = 19.sp, fontWeight = FontWeight.Medium)
            }
            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge { Text(if (unreadCount > 9) "9+" else "$unreadCount") }
                    }
                }
            ) {
                IconButton(onClick = onOpenNotifications) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = stringResource(R.string.notifications),
                        tint = CsmTheme.colors.textSecondary
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            UserAvatar(name = user.name, photoUri = user.photoUri, size = 42.dp)
        }

        NewRequestCta(onClick = onNewRequest)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(Modifier.weight(1f), submitted.toString(), stringResource(R.string.stat_submitted), CsmBlue)
            StatCard(Modifier.weight(1f), underReview.toString(), stringResource(R.string.status_review), StatusReviewDot)
            StatCard(Modifier.weight(1f), completed.toString(), stringResource(R.string.stat_completed), StatusDoneDot)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.recent_requests), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
            Text(
                stringResource(R.string.see_all),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = CsmBlue,
                modifier = Modifier.clickable { onSeeAllRequests() }
            )
        }

        if (recent.isEmpty()) {
            EmptyHint(stringResource(R.string.empty_no_requests))
        } else {
            recent.forEach { item ->
                RequestCard(item = item, onClick = { onOpenRequest(item.request.id) })
            }
        }
    }
}

@Composable
private fun NewRequestCta(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = CsmBlue,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.new_request), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    stringResource(R.string.new_request_subtitle),
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, label: String, valueColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CsmTheme.colors.surface,
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = valueColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, color = CsmTheme.colors.textMuted, fontSize = 11.sp)
        }
    }
}

@Composable
fun EmptyHint(text: String) {
    Text(
        text = text,
        color = CsmTheme.colors.textMuted,
        fontSize = 13.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 8.dp)
    )
}
