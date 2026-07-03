package pt.ipvc.csm.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
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
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.CsmTextMuted
import pt.ipvc.csm.ui.theme.CsmTextPrimary
import pt.ipvc.csm.ui.theme.StatusDoneDot
import pt.ipvc.csm.ui.theme.StatusReviewDot

@Composable
fun UserHomeTab(
    user: UserEntity,
    requests: List<RequestWithDetails>,
    onNewRequest: () -> Unit,
    onOpenRequest: (Long) -> Unit,
    onSeeAllRequests: () -> Unit
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
                Text("Olá,", color = CsmTextMuted, fontSize = 12.sp)
                Text(user.name, color = CsmTextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Medium)
            }
            UserAvatar(name = user.name, photoUri = user.photoUri, size = 42.dp)
        }

        NewRequestCta(onClick = onNewRequest)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(Modifier.weight(1f), submitted.toString(), "Submetidos", CsmBlue)
            StatCard(Modifier.weight(1f), underReview.toString(), "Em análise", StatusReviewDot)
            StatCard(Modifier.weight(1f), completed.toString(), "Concluídos", StatusDoneDot)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pedidos recentes", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CsmTextPrimary)
            Text(
                "Ver todos",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = CsmBlue,
                modifier = Modifier.clickable { onSeeAllRequests() }
            )
        }

        if (recent.isEmpty()) {
            EmptyHint("Ainda não tens pedidos. Toca em \"Novo pedido\" para começar.")
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
                Text("Novo pedido", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "Reporta uma ocorrência no campus",
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
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = valueColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, color = CsmTextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
fun EmptyHint(text: String) {
    Text(
        text = text,
        color = CsmTextMuted,
        fontSize = 13.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 8.dp)
    )
}
