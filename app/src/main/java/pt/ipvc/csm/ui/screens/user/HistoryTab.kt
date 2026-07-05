package pt.ipvc.csm.ui.screens.user

import androidx.compose.ui.res.stringResource
import pt.ipvc.csm.R
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
import pt.ipvc.csm.model.Priority
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.ui.components.CategoryFilterRow
import pt.ipvc.csm.ui.components.CsmFilterChip
import pt.ipvc.csm.ui.components.PriorityFilterRow
import pt.ipvc.csm.ui.components.RequestCard

@Composable
fun HistoryTab(
    requests: List<RequestWithDetails>,
    onOpenRequest: (Long) -> Unit
) {
    var statusFilter by remember { mutableStateOf<RequestStatus?>(null) }
    var categoryFilter by remember { mutableStateOf<Long?>(null) }
    var priorityFilter by remember { mutableStateOf<Priority?>(null) }

    val historyBase = requests.filter { it.request.status.isHistory }
    val history = historyBase
        .filter { statusFilter == null || it.request.status == statusFilter }
        .filter { categoryFilter == null || it.request.categoryId == categoryFilter }
        .filter { priorityFilter == null || it.request.priority == priorityFilter }
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
                stringResource(R.string.nav_history),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = CsmTheme.colors.textPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CsmFilterChip(stringResource(R.string.filter_all), statusFilter == null) { statusFilter = null }
                CsmFilterChip(stringResource(R.string.status_completed), statusFilter == RequestStatus.CONCLUIDO) {
                    statusFilter = RequestStatus.CONCLUIDO
                }
                CsmFilterChip(stringResource(R.string.status_rejected), statusFilter == RequestStatus.REJEITADO) {
                    statusFilter = RequestStatus.REJEITADO
                }
                CsmFilterChip(stringResource(R.string.status_cancelled), statusFilter == RequestStatus.CANCELADO) {
                    statusFilter = RequestStatus.CANCELADO
                }
            }
        }
        item { CategoryFilterRow(historyBase, categoryFilter) { categoryFilter = it } }
        item { PriorityFilterRow(priorityFilter) { priorityFilter = it } }

        if (history.isEmpty()) {
            item { EmptyHint(stringResource(R.string.empty_history)) }
        } else {
            items(history, key = { it.request.id }) { item ->
                RequestCard(item = item, onClick = { onOpenRequest(item.request.id) })
            }
        }
    }
}
