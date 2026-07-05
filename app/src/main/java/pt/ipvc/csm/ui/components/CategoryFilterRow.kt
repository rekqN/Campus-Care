package pt.ipvc.csm.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ipvc.csm.R
import pt.ipvc.csm.data.local.RequestWithDetails

/**
 * A horizontally-scrolling chip row for filtering a request list by category. Only the categories
 * that actually appear in [requests] are offered; the row hides itself when there is nothing to
 * choose between (fewer than two distinct categories).
 */
@Composable
fun CategoryFilterRow(
    requests: List<RequestWithDetails>,
    selectedCategoryId: Long?,
    modifier: Modifier = Modifier,
    onSelect: (Long?) -> Unit
) {
    val categories = remember(requests) {
        requests
            .mapNotNull { item -> item.request.categoryId?.let { id -> id to (item.categoryName ?: "") } }
            .distinctBy { it.first }
            .sortedBy { it.second.lowercase() }
    }
    if (categories.size < 2) return

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CsmFilterChip(stringResource(R.string.all_categories), selectedCategoryId == null) { onSelect(null) }
        categories.forEach { (id, name) ->
            CsmFilterChip(name, selectedCategoryId == id) { onSelect(id) }
        }
    }
}
