package pt.ipvc.csm.ui.screens.user

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipvc.csm.data.local.RequestWithDetails
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.ui.components.CsmFilterChip
import pt.ipvc.csm.ui.components.RequestCard
import pt.ipvc.csm.ui.theme.CsmTextPrimary

@Composable
fun HistoryTab(
    requests: List<RequestWithDetails>,
    onOpenRequest: (Long) -> Unit
) {
    var statusFilter by remember { mutableStateOf<RequestStatus?>(null) }

    val history = requests
        .filter { it.request.status.isHistory }
        .filter { statusFilter == null || it.request.status == statusFilter }
        .sortedByDescending { it.request.updatedAt }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Histórico",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = CsmTextPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CsmFilterChip("Todos", statusFilter == null) { statusFilter = null }
                CsmFilterChip("Concluído", statusFilter == RequestStatus.CONCLUIDO) {
                    statusFilter = RequestStatus.CONCLUIDO
                }
                CsmFilterChip("Rejeitado", statusFilter == RequestStatus.REJEITADO) {
                    statusFilter = RequestStatus.REJEITADO
                }
                CsmFilterChip("Cancelado", statusFilter == RequestStatus.CANCELADO) {
                    statusFilter = RequestStatus.CANCELADO
                }
            }
        }

        if (history.isEmpty()) {
            item { EmptyHint("Ainda não há pedidos no histórico.") }
        } else {
            items(history, key = { it.request.id }) { item ->
                RequestCard(item = item, onClick = { onOpenRequest(item.request.id) })
            }
        }
    }
}
