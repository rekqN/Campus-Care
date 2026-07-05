package pt.ipvc.csm.ui.screens.admin

import androidx.compose.ui.res.stringResource
import pt.ipvc.csm.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.PriorityHigh
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
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.ui.components.RequestCard
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.PriorityUrgentBg
import pt.ipvc.csm.ui.theme.PriorityUrgentDot
import pt.ipvc.csm.ui.theme.PriorityUrgentFg
import pt.ipvc.csm.ui.theme.StatusDoneDot
import pt.ipvc.csm.ui.theme.StatusRejectedDot
import pt.ipvc.csm.ui.theme.StatusReviewDot
import pt.ipvc.csm.ui.theme.StatusSubmittedDot

@Composable
fun AdminDashboardTab(
    requests: List<RequestWithDetails>,
    onOpenRequest: (Long) -> Unit,
    onSeeAllRequests: () -> Unit,
    onExport: () -> Unit
) {
    val total = requests.size
    fun count(status: RequestStatus) = requests.count { it.request.status == status }
    val submitted = count(RequestStatus.SUBMETIDO)
    val review = count(RequestStatus.EM_ANALISE)
    val done = count(RequestStatus.CONCLUIDO)
    val rejected = count(RequestStatus.REJEITADO)
    val highOpen = requests.count { it.request.status.isActive && it.request.priority.isElevated }
    val topCategories = requests
        .groupBy { it.categoryName }
        .map { (name, list) -> name to list.size }
        .sortedByDescending { it.second }
        .take(5)
    val recent = requests.take(4)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.administrator_caps),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CsmBlue,
                    letterSpacing = 1.2.sp
                )
                Text(stringResource(R.string.requests_panel), fontSize = 19.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
            }
            IconButton(onClick = onExport) {
                Icon(Icons.Outlined.Download, contentDescription = stringResource(R.string.export), tint = CsmTheme.colors.textSecondary)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(Modifier.weight(1f), stringResource(R.string.stat_total), total, CsmBlue)
            StatCard(Modifier.weight(1f), stringResource(R.string.stat_submitted), submitted, StatusSubmittedDot)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(Modifier.weight(1f), stringResource(R.string.status_review), review, StatusReviewDot)
            StatCard(Modifier.weight(1f), stringResource(R.string.stat_completed), done, StatusDoneDot)
        }

        HighPriorityCard(highOpen)

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = CsmTheme.colors.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                Text(stringResource(R.string.requests_by_status), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
                StatusBar(stringResource(R.string.status_submitted), submitted, total, StatusSubmittedDot)
                StatusBar(stringResource(R.string.status_review), review, total, StatusReviewDot)
                StatusBar(stringResource(R.string.status_completed), done, total, StatusDoneDot)
                StatusBar(stringResource(R.string.status_rejected), rejected, total, StatusRejectedDot)
            }
        }

        if (total > 0) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CsmTheme.colors.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                    Text(stringResource(R.string.requests_by_category), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
                    topCategories.forEach { (name, value) ->
                        StatusBar(name ?: stringResource(R.string.no_category), value, total, CsmBlue)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.recent), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
            Text(
                stringResource(R.string.see_all),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = CsmBlue,
                modifier = Modifier.clickable { onSeeAllRequests() }
            )
        }

        if (recent.isEmpty()) {
            Text(
                stringResource(R.string.empty_no_submitted),
                color = CsmTheme.colors.textMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            recent.forEach { item ->
                RequestCard(item = item, onClick = { onOpenRequest(item.request.id) }, showAuthor = true)
            }
        }
    }
}

/** Highlights how many active requests are Alta/Urgente. Goes red when there are any. */
@Composable
private fun HighPriorityCard(count: Int) {
    val active = count > 0
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (active) PriorityUrgentBg else CsmTheme.colors.surface,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) PriorityUrgentDot.copy(alpha = 0.18f) else CsmTheme.colors.surfaceFill),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.PriorityHigh,
                    contentDescription = null,
                    tint = if (active) PriorityUrgentFg else CsmTheme.colors.textMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
            if (active) {
                Text(count.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PriorityUrgentFg)
                Text(
                    stringResource(R.string.open_high_priority),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PriorityUrgentFg,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    stringResource(R.string.no_high_priority_open),
                    fontSize = 13.sp,
                    color = CsmTheme.colors.textMuted,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: Int, dotColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CsmTheme.colors.surface,
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Text(label, fontSize = 11.5.sp, color = CsmTheme.colors.textMuted)
            }
            Text(
                value.toString(),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = CsmTheme.colors.textPrimary,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Composable
private fun StatusBar(label: String, value: Int, total: Int, color: Color) {
    val fraction = if (total > 0) value.toFloat() / total else 0f
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(label, fontSize = 12.5.sp, color = CsmTheme.colors.textMuted, modifier = Modifier.width(82.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(CsmTheme.colors.surfaceFill)
        ) {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color)
                )
            }
        }
        Text(
            value.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = CsmTheme.colors.textPrimary,
            modifier = Modifier.width(24.dp)
        )
    }
}
