package pt.ipvc.csm.ui.screens.admin

import pt.ipvc.csm.ui.theme.CsmTheme
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
import pt.ipvc.csm.ui.components.CsmSearchBar
import pt.ipvc.csm.ui.components.RequestCard
import pt.ipvc.csm.ui.screens.user.EmptyHint
import pt.ipvc.csm.ui.screens.user.matchesQuery

@Composable
fun AllRequestsTab(
    requests: List<RequestWithDetails>,
    onOpenRequest: (Long) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<RequestStatus?>(null) }

    val filtered = requests
        .filter { statusFilter == null || it.request.status == statusFilter }
        .filter { it.matchesQuery(query) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Todos os pedidos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = CsmTheme.colors.textPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        item { CsmSearchBar(query = query, onQueryChange = { query = it }, placeholder = "Pesquisar pedidos…") }
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CsmFilterChip("Todos", statusFilter == null) { statusFilter = null }
                RequestStatus.entries.forEach { status ->
                    CsmFilterChip(status.ptLabel, statusFilter == status) { statusFilter = status }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                EmptyHint(
                    if (requests.isEmpty()) "Ainda não há pedidos submetidos."
                    else "Nenhum pedido corresponde ao filtro."
                )
            }
        } else {
            items(filtered, key = { it.request.id }) { item ->
                RequestCard(item = item, onClick = { onOpenRequest(item.request.id) }, showAuthor = true)
            }
        }
    }
}
