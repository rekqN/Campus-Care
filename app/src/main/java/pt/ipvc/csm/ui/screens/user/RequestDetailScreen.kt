package pt.ipvc.csm.ui.screens.user

import pt.ipvc.csm.ui.theme.CsmTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pt.ipvc.csm.data.local.StatusHistoryWithAuthor
import pt.ipvc.csm.ui.components.IconTile
import pt.ipvc.csm.ui.components.StatusChip
import pt.ipvc.csm.ui.components.iconForKey
import pt.ipvc.csm.ui.components.paletteFor
import pt.ipvc.csm.util.DateUtils
import pt.ipvc.csm.viewmodel.UserViewModel
import java.io.File

@Composable
fun RequestDetailScreen(
    userViewModel: UserViewModel,
    requestId: Long,
    onBack: () -> Unit
) {
    val detailsFlow = remember(requestId) { userViewModel.requestDetails(requestId) }
    val historyFlow = remember(requestId) { userViewModel.statusHistory(requestId) }
    val details by detailsFlow.collectAsState(initial = null)
    val history by historyFlow.collectAsState(initial = emptyList())

    var showCancelDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = CsmTheme.colors.textPrimary)
            }
            Text(
                "Pedido #${requestId}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = CsmTheme.colors.textPrimary
            )
        }

        val current = details
        if (current == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        val request = current.request
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconTile(iconForKey(current.categoryIcon), tileSize = 46.dp, corner = 14.dp)
                Column {
                    Text(request.title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
                    Spacer(Modifier.height(4.dp))
                    StatusChip(request.status)
                }
            }

            if (request.photoUri != null) {
                AsyncImage(
                    model = File(request.photoUri),
                    contentDescription = "Fotografia do pedido",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CsmTheme.colors.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InfoRow("Categoria", current.categoryName ?: "Sem categoria")
                    InfoRow("Localização", request.location)
                    InfoRow("Criado em", DateUtils.formatDateTime(request.createdAt))
                    HorizontalDivider(color = CsmTheme.colors.divider)
                    Column {
                        Text("Descrição", fontSize = 12.5.sp, color = CsmTheme.colors.textTertiary)
                        Text(
                            request.description,
                            fontSize = 13.sp,
                            color = CsmTheme.colors.textPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Text("Estado do pedido", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
            StatusTimeline(history)

            if (request.status.isActive) {
                Spacer(Modifier.height(4.dp))
                pt.ipvc.csm.ui.components.DangerPillButton(
                    text = "Cancelar pedido",
                    onClick = { showCancelDialog = true },
                    leadingIcon = Icons.Outlined.Cancel
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar pedido") },
            text = { Text("Tens a certeza que queres cancelar este pedido? Esta ação não pode ser revertida.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    userViewModel.cancelRequest(requestId) { }
                }) { Text("Cancelar pedido") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Voltar") }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.5.sp, color = CsmTheme.colors.textTertiary)
        Text(value, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
    }
}

@Composable
private fun StatusTimeline(history: List<StatusHistoryWithAuthor>) {
    if (history.isEmpty()) {
        Text("Sem histórico de estado.", fontSize = 12.sp, color = CsmTheme.colors.textFaint)
        return
    }
    Column {
        history.forEachIndexed { index, entry ->
            val palette = paletteFor(entry.entry.status)
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(palette.dot)
                    )
                    if (index < history.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(CsmTheme.colors.divider)
                        )
                    }
                }
                Column(modifier = Modifier.padding(start = 12.dp, bottom = if (index < history.lastIndex) 10.dp else 0.dp)) {
                    Text(entry.entry.status.ptLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = palette.fg)
                    Text(
                        "${DateUtils.formatTimeline(entry.entry.changedAt)} · ${entry.changedByName}",
                        fontSize = 11.sp,
                        color = CsmTheme.colors.textFaint
                    )
                }
            }
        }
    }
}
