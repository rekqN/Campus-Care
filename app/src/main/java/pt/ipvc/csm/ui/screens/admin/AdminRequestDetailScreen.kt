package pt.ipvc.csm.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
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
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.ui.components.DangerPillButton
import pt.ipvc.csm.ui.components.IconTile
import pt.ipvc.csm.ui.components.PrimaryButton
import pt.ipvc.csm.ui.components.iconForKey
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.CsmBlueContainer
import pt.ipvc.csm.ui.theme.CsmBlueDark
import pt.ipvc.csm.ui.theme.CsmDanger
import pt.ipvc.csm.ui.theme.CsmError
import pt.ipvc.csm.util.DateUtils
import pt.ipvc.csm.viewmodel.AdminViewModel
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminRequestDetailScreen(
    adminViewModel: AdminViewModel,
    requestId: Long,
    onBack: () -> Unit
) {
    val detailsFlow = remember(requestId) { adminViewModel.requestDetails(requestId) }
    val details by detailsFlow.collectAsState(initial = null)
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = CsmTheme.colors.textPrimary)
            }
            Text(
                "Pedido #$requestId",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = CsmTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { confirmDelete = true }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = CsmDanger)
            }
        }

        val current = details
        if (current == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        val request = current.request
        var selected by remember(request.status) { mutableStateOf(request.status) }

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
                    Text(
                        "${current.categoryName ?: "Sem categoria"} · por ${current.userName}",
                        fontSize = 12.sp,
                        color = CsmTheme.colors.textTertiary
                    )
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
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

            Text("Alterar estado", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RequestStatus.adminAssignable.forEach { status ->
                    StateChoiceChip(
                        label = status.ptLabel,
                        selected = selected == status,
                        onClick = { selected = status }
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            PrimaryButton(
                text = "Guardar alterações",
                onClick = { adminViewModel.changeStatus(requestId, selected) { } }
            )
            DangerPillButton(
                text = "Eliminar pedido",
                onClick = { confirmDelete = true },
                leadingIcon = Icons.Outlined.Delete
            )
            Spacer(Modifier.height(20.dp))
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Eliminar pedido") },
            text = { Text("Tens a certeza que queres eliminar este pedido? Esta ação não pode ser revertida.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    adminViewModel.deleteRequest(requestId) { onBack() }
                }) { Text("Eliminar", color = CsmError) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Voltar") }
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
private fun StateChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) CsmBlueContainer else CsmTheme.colors.surface)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) CsmBlue else CsmTheme.colors.outline,
                shape = RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (selected) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = CsmBlueDark, modifier = Modifier.size(17.dp))
        }
        Text(
            label,
            color = if (selected) CsmBlueDark else CsmTheme.colors.textSecondary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
