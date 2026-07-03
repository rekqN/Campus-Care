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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapVert
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
import pt.ipvc.csm.ui.theme.CsmTextPrimary

/** Returns true if the request matches a free-text query over title/location/category. */
internal fun RequestWithDetails.matchesQuery(query: String): Boolean {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return true
    return request.title.lowercase().contains(q) ||
        request.location.lowercase().contains(q) ||
        (categoryName?.lowercase()?.contains(q) == true)
}

@Composable
fun MyRequestsTab(
    requests: List<RequestWithDetails>,
    onOpenRequest: (Long) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<RequestStatus?>(null) }
    var newestFirst by remember { mutableStateOf(true) }

    val filtered = requests
        .filter { it.request.status.isActive }
        .filter { statusFilter == null || it.request.status == statusFilter }
        .filter { it.matchesQuery(query) }
        .let { list -> if (newestFirst) list.sortedByDescending { it.request.createdAt } else list.sortedBy { it.request.createdAt } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Os meus pedidos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = CsmTextPrimary,
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
                CsmFilterChip("Submetido", statusFilter == RequestStatus.SUBMETIDO) {
                    statusFilter = RequestStatus.SUBMETIDO
                }
                CsmFilterChip("Em análise", statusFilter == RequestStatus.EM_ANALISE) {
                    statusFilter = RequestStatus.EM_ANALISE
                }
                CsmFilterChip(
                    label = if (newestFirst) "Mais recentes" else "Mais antigos",
                    selected = false,
                    onClick = { newestFirst = !newestFirst },
                    leadingIcon = Icons.Outlined.SwapVert
                )
            }
        }

        if (filtered.isEmpty()) {
            item {
                EmptyHint(
                    if (requests.any { it.request.status.isActive })
                        "Nenhum pedido corresponde ao filtro."
                    else
                        "Ainda não tens pedidos ativos."
                )
            }
        } else {
            items(filtered, key = { it.request.id }) { item ->
                RequestCard(item = item, onClick = { onOpenRequest(item.request.id) })
            }
        }
    }
}
