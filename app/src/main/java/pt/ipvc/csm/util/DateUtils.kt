package pt.ipvc.csm.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Portuguese date formatting helpers used across the screens. */
object DateUtils {
    private val ptPT = Locale("pt", "PT")
    private val fullFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", ptPT)
    private val shortFormat = SimpleDateFormat("dd MMM", ptPT)
    private val timelineFormat = SimpleDateFormat("dd MMM, HH:mm", ptPT)

    /** e.g. "12 mai 2026, 09:24" */
    fun formatDateTime(millis: Long): String = fullFormat.format(Date(millis))

    /** e.g. "12 mai" */
    fun formatShortDate(millis: Long): String = shortFormat.format(Date(millis))

    /** e.g. "12 mai, 14:10" — used in the status timeline. */
    fun formatTimeline(millis: Long): String = timelineFormat.format(Date(millis))

    /** e.g. "agora", "há 5 min", "há 2 h", "ontem", "há 3 dias", or a short date. */
    fun formatRelative(millis: Long): String {
        val diff = System.currentTimeMillis() - millis
        val minutes = diff / 60_000
        val hours = minutes / 60
        val days = hours / 24
        return when {
            minutes < 1 -> "agora"
            minutes < 60 -> "há $minutes min"
            hours < 24 -> "há $hours h"
            days == 1L -> "ontem"
            days < 7 -> "há $days dias"
            else -> shortFormat.format(Date(millis))
        }
    }
}
