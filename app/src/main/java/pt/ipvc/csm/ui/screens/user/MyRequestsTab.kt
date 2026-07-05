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
import pt.ipvc.csm.ui.components.CategoryFilterRow
import pt.ipvc.csm.ui.components.CsmFilterChip
import pt.ipvc.csm.ui.components.CsmSearchBar
import pt.ipvc.csm.ui.components.RequestCard

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
    var categoryFilter by remember { mutableStateOf<Long?>(null) }
    var newestFirst by remember { mutableStateOf(true) }

    val active = requests.filter { it.request.status.isActive }
    val filtered = active
        .filter { statusFilter == null || it.request.status == statusFilter }
        .filter { categoryFilter == null || it.request.categoryId == categoryFilter }
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
                stringResource(R.string.my_requests),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = CsmTheme.colors.textPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        item { CsmSearchBar(query = query, onQueryChange = { query = it }, placeholder = stringResource(R.string.search_requests)) }
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CsmFilterChip(stringResource(R.string.filter_all), statusFilter == null) { statusFilter = null }
                CsmFilterChip(stringResource(R.string.status_submitted), statusFilter == RequestStatus.SUBMETIDO) {
                    statusFilter = RequestStatus.SUBMETIDO
                }
                CsmFilterChip(stringResource(R.string.status_review), statusFilter == RequestStatus.EM_ANALISE) {
                    statusFilter = RequestStatus.EM_ANALISE
                }
                CsmFilterChip(
                    label = if (newestFirst) stringResource(R.string.sort_newest) else stringResource(R.string.sort_oldest),
                    selected = false,
                    onClick = { newestFirst = !newestFirst },
                    leadingIcon = Icons.Outlined.SwapVert
                )
            }
        }
        item { CategoryFilterRow(active, categoryFilter) { categoryFilter = it } }

        if (filtered.isEmpty()) {
            item {
                EmptyHint(
                    if (requests.any { it.request.status.isActive })
                        stringResource(R.string.empty_no_match)
                    else
                        stringResource(R.string.empty_no_active)
                )
            }
        } else {
            items(filtered, key = { it.request.id }) { item ->
                RequestCard(item = item, onClick = { onOpenRequest(item.request.id) })
            }
        }
    }
}
