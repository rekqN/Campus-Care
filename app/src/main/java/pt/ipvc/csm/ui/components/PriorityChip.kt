package pt.ipvc.csm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipvc.csm.R
import pt.ipvc.csm.model.Priority
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.PriorityHighBg
import pt.ipvc.csm.ui.theme.PriorityHighDot
import pt.ipvc.csm.ui.theme.PriorityHighFg
import pt.ipvc.csm.ui.theme.PriorityLowBg
import pt.ipvc.csm.ui.theme.PriorityLowDot
import pt.ipvc.csm.ui.theme.PriorityLowFg
import pt.ipvc.csm.ui.theme.PriorityMediumBg
import pt.ipvc.csm.ui.theme.PriorityMediumDot
import pt.ipvc.csm.ui.theme.PriorityMediumFg
import pt.ipvc.csm.ui.theme.PriorityUrgentBg
import pt.ipvc.csm.ui.theme.PriorityUrgentDot
import pt.ipvc.csm.ui.theme.PriorityUrgentFg

data class PriorityPalette(val bg: Color, val fg: Color, val dot: Color)

fun paletteFor(priority: Priority): PriorityPalette = when (priority) {
    Priority.BAIXA -> PriorityPalette(PriorityLowBg, PriorityLowFg, PriorityLowDot)
    Priority.MEDIA -> PriorityPalette(PriorityMediumBg, PriorityMediumFg, PriorityMediumDot)
    Priority.ALTA -> PriorityPalette(PriorityHighBg, PriorityHighFg, PriorityHighDot)
    Priority.URGENTE -> PriorityPalette(PriorityUrgentBg, PriorityUrgentFg, PriorityUrgentDot)
}

/** The colored pill (dot + label) used wherever a request priority is shown. */
@Composable
fun PriorityChip(priority: Priority, modifier: Modifier = Modifier) {
    val palette = paletteFor(priority)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(palette.bg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(palette.dot)
        )
        Text(
            text = priorityLabel(priority),
            color = palette.fg,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/** A horizontally-scrolling chip row for filtering a request list by priority ("All" + each level). */
@Composable
fun PriorityFilterRow(
    selected: Priority?,
    modifier: Modifier = Modifier,
    onSelect: (Priority?) -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CsmFilterChip(stringResource(R.string.filter_all), selected == null) { onSelect(null) }
        Priority.entries.forEach { priority ->
            CsmFilterChip(priorityLabel(priority), selected == priority) { onSelect(priority) }
        }
    }
}

/** Single-select priority picker: one chip per priority, the selected one filled with its color. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrioritySelector(
    selected: Priority,
    onSelect: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Priority.entries.forEach { priority ->
            val palette = paletteFor(priority)
            val isSelected = priority == selected
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) palette.bg else CsmTheme.colors.surface)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) palette.dot else CsmTheme.colors.outline,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onSelect(priority) }
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(palette.dot)
                )
                Text(
                    text = priorityLabel(priority),
                    color = if (isSelected) palette.fg else CsmTheme.colors.textSecondary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
