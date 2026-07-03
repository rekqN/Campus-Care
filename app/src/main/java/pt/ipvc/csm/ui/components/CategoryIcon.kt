package pt.ipvc.csm.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

/** Maps a stored category iconKey to a Material icon (falls back to a generic category icon). */
fun iconForKey(key: String?): ImageVector = when (key) {
    "build" -> Icons.Outlined.Build
    "cleaning_services" -> Icons.Outlined.CleaningServices
    "security" -> Icons.Outlined.Security
    "computer" -> Icons.Outlined.Computer
    "meeting_room" -> Icons.Outlined.MeetingRoom
    "wifi" -> Icons.Outlined.Wifi
    "chair" -> Icons.Outlined.Chair
    "local_library" -> Icons.Outlined.LocalLibrary
    "science" -> Icons.Outlined.Science
    "directions_car" -> Icons.Outlined.DirectionsCar
    "restaurant" -> Icons.Outlined.Restaurant
    "report_problem" -> Icons.Outlined.ReportProblem
    else -> Icons.Outlined.Category
}

/** The icon options offered when creating/editing a category. */
val categoryIconKeys = listOf(
    "build",
    "cleaning_services",
    "security",
    "computer",
    "meeting_room",
    "wifi",
    "chair",
    "local_library",
    "science",
    "directions_car",
    "restaurant",
    "report_problem"
)
